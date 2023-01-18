package org.coderclan.guidepost.datasource;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.core.env.Environment;

import java.util.Objects;

/**
 * TODO change me.
 *
 * @author TODO
 * @date 2023/1/17
 */
public class HikariDataSourceBuilder implements DataSourceBuilder {
    @Autowired
    private Environment environment;

    @Autowired
    private DataSourceProperties properties;

    private static final Logger log = LoggerFactory.getLogger(HikariDataSourceBuilder.class);

    private static final String CONFIG_PREFIX = "spring.datasource.hikari.";

    @Override
    public NamedDataSource create() {
        String address = getAddress(properties);
        return new NamedDataSource(address, createUnderlyingDataSource());
    }

    private HikariDataSource createUnderlyingDataSource() {
        HikariDataSource ds = properties.initializeDataSourceBuilder().type(HikariDataSource.class).build();

        final Integer connectionTimeout = environment.getProperty(CONFIG_PREFIX + "connectionTimeout", Integer.class);
        if (Objects.nonNull(connectionTimeout)) {
            ds.setConnectionTimeout(connectionTimeout);
        }

        final Integer idleTimeout = environment.getProperty(CONFIG_PREFIX + "idleTimeout", Integer.class);
        if (Objects.nonNull(idleTimeout)) {
            ds.setIdleTimeout(idleTimeout);
        }

        final Integer maxLifetime = environment.getProperty(CONFIG_PREFIX + "maxLifetime", Integer.class);
        if (Objects.nonNull(maxLifetime)) {
            ds.setMaxLifetime(maxLifetime);
        }


        final String connectionTestQuery = environment.getProperty(CONFIG_PREFIX + "connectionTestQuery", String.class);
        if (Objects.nonNull(connectionTestQuery)) {
            ds.setConnectionTestQuery(connectionTestQuery);
        }
        final Integer minimumIdle = environment.getProperty(CONFIG_PREFIX + "minimumIdle", Integer.class);
        if (Objects.nonNull(minimumIdle)) {
            ds.setMinimumIdle(minimumIdle);
        }
        final Integer maximumPoolSize = environment.getProperty(CONFIG_PREFIX + "maximumPoolSize", Integer.class);
        if (Objects.nonNull(maximumPoolSize)) {
            ds.setMaximumPoolSize(maximumPoolSize);
        }

        // This is very important to set AutoCommit to false
        // If this value is true. TransactionSynchronizationManager.isCurrentTransactionReadOnly() will return FALSE always.
        ds.setAutoCommit(false);


        return ds;
    }

    public NamedDataSource create(String address) {

        HikariDataSource ds = createUnderlyingDataSource();


        String jdbcUrl = properties.determineUrl().replaceFirst("://[^/]+", "://" + address);
        ds.setJdbcUrl(jdbcUrl);

        log.trace("Created underlying DataSource of Database node: {}", address);

        return new NamedDataSource(address, ds);
    }

    private String getAddress(DataSourceProperties properties) {
        String url = properties.getUrl();

        // JDBC URL should be like foo:boor:buz://host:port/xxx
        int beginIndex = url.indexOf("://") + "://".length();
        int endIndex = url.indexOf('/', beginIndex);
        return url.substring(beginIndex, endIndex);
    }
}
