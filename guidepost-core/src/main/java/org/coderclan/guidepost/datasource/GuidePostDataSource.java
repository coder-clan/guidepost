package org.coderclan.guidepost.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/5
 */
public class GuidePostDataSource extends AbstractDataSourceWrapper {
    private static Logger log = LoggerFactory.getLogger(GuidePostDataSource.class);
    private DataSourceChecker dataSourceChecker;

    @Autowired
    private DatabaseNodeDiscovery discovery;

    @Autowired
    private DataSourceProperties properties;


    @Autowired
    private DataSourceBuilder dataSourceBuilder;


    /**
     * TODO support other DataSources.
     *
     * @param properties
     * @return
     */
    public HikariDataSource createDataSource(DataSourceProperties properties) {

        HikariDataSource ds = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        // This is very important to set AutoCommit to false
        // If this value is true. TransactionSynchronizationManager.isCurrentTransactionReadOnly() will return FALSE always.
        ds.setAutoCommit(false);


//        HikariDataSource ds = new HikariDataSource();
//        ds.setJdbcUrl(properties.getUrl());
//        ds.setUsername(properties.getUsername());
//        ds.setPassword(properties.getPassword());
//        ds.setPoolName(properties.getName());
//        ds.setAutoCommit(false);
//        if (Objects.nonNull(properties.getDriverClassName())) {
//            ds.setDriverClassName(properties.getDriverClassName());
//        }
        return ds;
    }

    @PostConstruct
    public void init() {
        NamedDataSource seedDataSource = this.dataSourceBuilder.create();

        this.discovery.setSeed(seedDataSource);
        this.discovery.run();
    }

    private String getAddress(DataSourceProperties properties) {
        String url = properties.getUrl();

        // JDBC URL should be like foo:boor:buz://host:port/xxx
        int beginIndex = url.indexOf("://") + "://".length();
        int endIndex = url.indexOf('/', beginIndex);
        return url.substring(beginIndex, endIndex);
    }

    @Override
    protected DataSource getDatasource() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        log.debug("isCurrentTransactionReadOnly: {}", readOnly);

        List<NamedDataSource> candidates = readOnly ? discovery.getReadOnlyDataSources() : discovery.getWritableDataSources();

        if (readOnly) {
            candidates = discovery.getReadOnlyDataSources();
            if (Objects.isNull(candidates) || candidates.isEmpty()) {
                log.warn("There is no underlying readonly DataSources for readonly Transaction, use WRITABLE DataSources instead.");
                candidates = discovery.getWritableDataSources();
            }
        } else {
            candidates = discovery.getWritableDataSources();
        }

        if (Objects.isNull(candidates) || candidates.isEmpty()) {
            throw new RuntimeException("There is no underlying DataSources.");
        }

        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        int random = ThreadLocalRandom.current().nextInt(candidates.size());
        return candidates.get(random);
    }

}
