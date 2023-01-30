package org.coderclan.guidepost;


import org.coderclan.guidepost.datasource.*;
import org.coderclan.guidepost.mysql.InnoDbClusterNodeDiscovery;
import org.coderclan.guidepost.mysql.MysqlDataSourceChecker;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.PropertySource;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * Configuration for the Guidepost.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/5
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class})
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
@PropertySource(value = "classpath:org/codeclan/guidepost/guidepost.properties", encoding = "UTF-8")
public class GuidepostConfiguration {

    @Bean
    @ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
    @ConditionalOnMissingBean
    public DataSourceChecker mysqlDataSourceChecker() {
        return new MysqlDataSourceChecker();
    }

    @Bean
    @ConditionalOnMissingBean
    public ReadWriteDetector springTransactionReadWriteDetector() {
        return new SpringTransactionReadWriteDetector();
    }

    @Bean
    @ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
    @ConditionalOnMissingBean
    public DatabaseNodeDiscovery databaseNodeDiscovery() {
        return new InnoDbClusterNodeDiscovery();
    }

    @Bean
    @ConditionalOnClass(name = "com.zaxxer.hikari.HikariDataSource")
    @ConditionalOnMissingBean
    public DataSourceBuilder dataSourceBuilder() {
        return new HikariDataSourceBuilder();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    @ConditionalOnBean({DatabaseNodeDiscovery.class, ReadWriteDetector.class, DataSourceBuilder.class})
    public DataSource guidePostRoutingDataSource() {
        return new GuidePostDataSource();
    }
}
