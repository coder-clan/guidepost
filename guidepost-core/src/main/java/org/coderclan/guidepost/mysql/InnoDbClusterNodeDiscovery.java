package org.coderclan.guidepost.mysql;

import org.coderclan.guidepost.datasource.DataSourceBuilder;
import org.coderclan.guidepost.datasource.DataSourceChecker;
import org.coderclan.guidepost.datasource.DatabaseNodeDiscovery;
import org.coderclan.guidepost.datasource.NamedDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import javax.sql.DataSource;
import java.io.Closeable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.concurrent.*;
import java.util.stream.Collectors;

/**
 * DatabaseNodeDiscovery implementation for MySQL InnoDB Cluster
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/14
 */
public class InnoDbClusterNodeDiscovery implements DatabaseNodeDiscovery {
    private static Logger log = LoggerFactory.getLogger(InnoDbClusterNodeDiscovery.class);

    @Autowired
    private DataSourceChecker checker;

    @Autowired
    private DataSourceBuilder dataSourceBuilder;

    /**
     * Interval of getting database nodes. Unit: second. default: 10
     */
    @Value("${org.coderclan.guidepost.discovery.refreshInterval:10}")
    private int refreshInterval = 10;
    private final List<NamedDataSource> readOnlyDataSources = new CopyOnWriteArrayList<>();
    private final List<NamedDataSource> writableDataSources = new CopyOnWriteArrayList<>();
    private final List<NamedDataSource> unmodifiableReadONlyDataSources = Collections.unmodifiableList(readOnlyDataSources);
    private final List<NamedDataSource> unmodifiableWritableDataSources = Collections.unmodifiableList(writableDataSources);

    /**
     * private use only.
     */
    private final HashMap<String, NamedDataSource> dataSourceMap = new HashMap<>();
    private boolean supported;
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor(new ThreadFactory() {
        @Override
        public Thread newThread(Runnable r) {
            Thread t = new Thread(r);
            t.setDaemon(true);
            t.setName("InnoDB-Discovery");
            return t;
        }
    });

    @Override
    public void setSeed(NamedDataSource seed) {
        this.supported = isMysql(seed);
        if (!this.supported) {
            log.error("The seed dataSource is not MySQL. Node Discovery is disabled!");
            this.writableDataSources.add(seed);
            return;
        }

        this.dataSourceMap.put(seed.getName(), seed);

        if (this.checker.isReadonly(seed)) {
            this.readOnlyDataSources.add(seed);
        } else {
            this.writableDataSources.add(seed);
        }
    }

    @Override
    public void run() {
        if (this.supported) {

            executorService.scheduleWithFixedDelay(this::refresh, 0, refreshInterval, TimeUnit.SECONDS);
        }
    }

    @Override
    public List<NamedDataSource> getWritableDataSources() {
        return unmodifiableWritableDataSources;
    }

    @Override
    public List<NamedDataSource> getReadOnlyDataSources() {
        return unmodifiableReadONlyDataSources;
    }

    private void refresh() {
        for (DataSource seed : this.readOnlyDataSources) {
            if (refresh(seed)) {
                return;
            }
        }
        for (DataSource seed : this.writableDataSources) {
            if (refresh(seed)) {
                return;
            }
        }
    }

    private boolean refresh(DataSource seed) {
        try (Connection connection = seed.getConnection(); Statement statement = connection.createStatement();) {
            ResultSet rs = statement.executeQuery("SELECT member_id,member_host,member_port,member_state,member_role FROM performance_schema.replication_group_members");
            HashSet<String> discoveredNodeAddresses = new HashSet<>();
            while (rs.next()) {
                final String address = rs.getString("member_host") + ":" + rs.getInt("member_port");
                String state = rs.getString("member_state");
                String role = rs.getString("member_role");
                discoveredNodeAddresses.add(address);
                log.trace("Found node: {}, state: {}, role: {}.", address, state, role);
            }

            statement.close();
            connection.close();

            // remove nodes;
            List<String> removedAddresses = this.dataSourceMap.keySet().stream().filter(e -> !discoveredNodeAddresses.contains(e)).collect(Collectors.toList());
            for (String removedAddress : removedAddresses) {
                log.info("Database node removed. Address: {}", removedAddress);

                NamedDataSource removedDataSource = this.dataSourceMap.remove(removedAddress);
                this.readOnlyDataSources.remove(removedDataSource);
                this.writableDataSources.remove(removedDataSource);

                if (removedDataSource instanceof Closeable) {
                    ((Closeable) removedDataSource).close();
                }
            }

            // check the writability.
            for (NamedDataSource ds : this.dataSourceMap.values()) {
                if (isReadonly(ds)) {
                    this.writableDataSources.remove(ds);
                    if (!this.readOnlyDataSources.contains(ds)) {
                        this.readOnlyDataSources.add(ds);
                    }
                } else {
                    this.readOnlyDataSources.remove(ds);
                    if (!this.writableDataSources.contains(ds)) {
                        this.writableDataSources.add(ds);
                    }
                }
            }

            // newly discovered nodes
            discoveredNodeAddresses.forEach(address -> {
                if (this.dataSourceMap.containsKey(address)) {
                    log.trace("Underlying DataSource of {} is already created", address);
                    return;
                }
                log.info("Database node added. Address: {}", address);
                createDataSource(address);
            });

            return true;
        } catch (Exception e) {
            log.error("", e);
            return false;
        }
    }

    private boolean isReadonly(NamedDataSource ds) {
        return this.checker.isReadonly(ds);
    }

    private void createDataSource(final String address) {
        NamedDataSource nds = this.dataSourceBuilder.create(address);

        if (isReadonly(nds)) {
            this.readOnlyDataSources.add(nds);
        } else {
            this.writableDataSources.add(nds);
        }

        this.dataSourceMap.put(address, nds);
    }

    public boolean isMysql(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection()) {
            DatabaseMetaData meta = connection.getMetaData();
            return "MySQL".equalsIgnoreCase(meta.getDatabaseProductName());
        } catch (Exception e) {
            log.error("Failed to determine if database is MySQL.", e);
            return false;
        }
    }
}
