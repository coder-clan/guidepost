package org.coderclan.guidepost;


import com.zaxxer.hikari.HikariDataSource;
import org.coderclan.guidepost.datasource.*;
import org.coderclan.guidepost.mysql.MysqlDataSourceChecker;
import org.coderclan.guidepost.mysql.MysqlDatabaseNodeDiscovery;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.jdbc.datasource.embedded.EmbeddedDatabaseType;

import javax.sql.DataSource;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/5
 */
@Configuration
@EnableConfigurationProperties({DataSourceProperties.class})
@ConditionalOnClass({DataSource.class, EmbeddedDatabaseType.class})
@AutoConfigureBefore({DataSourceAutoConfiguration.class})
public class DataSourceConfig {

    @Bean
    @ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
    @ConditionalOnMissingBean
    public DataSourceChecker mysqlDataSourceChecker() {
        return new MysqlDataSourceChecker();
    }

    @Bean
    @Primary
    @ConditionalOnMissingBean
    public DataSource guidePostRoutingDataSource() {
        return new GuidePostDataSource();
    }

    @Bean
    @ConditionalOnClass(name = "com.mysql.cj.jdbc.Driver")
    @ConditionalOnMissingBean
    public DatabaseNodeDiscovery databaseNodeDiscovery() {
        return new MysqlDatabaseNodeDiscovery();
    }

    @Bean
    @ConditionalOnClass(name="com.zaxxer.hikari.HikariDataSource")
    @ConditionalOnMissingBean
    public DataSourceBuilder dataSourceBuilder() {
        return new HikariDataSourceBuilder();
    }
}
