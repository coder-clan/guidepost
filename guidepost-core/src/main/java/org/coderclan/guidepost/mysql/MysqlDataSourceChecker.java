package org.coderclan.guidepost.mysql;

import org.coderclan.guidepost.datasource.DataSourceChecker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * DataSourceChecker implementation for MySQL
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/16
 */
public class MysqlDataSourceChecker implements DataSourceChecker {
    private static Logger log = LoggerFactory.getLogger(MysqlDataSourceChecker.class);

    @Override
    public boolean isReadonly(DataSource dataSource) {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement();
             ResultSet rs = statement.executeQuery("SELECT @@GLOBAL.read_only");
        ) {
            if (rs.next()) {
                final boolean readonly = rs.getBoolean(1);
                log.trace("Datasource：{}, readonly: {}", dataSource, readonly);
                return readonly;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Exception countered while determining writability of the DataSource.", e);
        }
        throw new RuntimeException("Can not determine whether the DataSource is writable.");
    }
}
