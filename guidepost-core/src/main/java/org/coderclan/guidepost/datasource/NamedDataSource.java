package org.coderclan.guidepost.datasource;

import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.util.Objects;
import java.util.logging.Logger;

/**
 * {@link DataSource} with identity.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/16
 */
public class NamedDataSource implements DataSource, Closeable {
    private static final org.slf4j.Logger log = LoggerFactory.getLogger(NamedDataSource.class);
    private String name;
    private DataSource delegated;

    /**
     * @param name      The name of the DataSource, should be unique.
     * @param delegated The underlying dataSource.
     */
    public NamedDataSource(String name, DataSource delegated) {
        this.name = name;
        this.delegated = delegated;
    }

    public String getName() {
        return name;
    }

    public Connection getConnection() throws SQLException {
        log.trace("Using DataSource: {}", this.name);
        return delegated.getConnection();
    }

    public Connection getConnection(String username, String password) throws SQLException {
        return delegated.getConnection(username, password);
    }

    public PrintWriter getLogWriter() throws SQLException {
        return delegated.getLogWriter();
    }

    public void setLogWriter(PrintWriter out) throws SQLException {
        delegated.setLogWriter(out);
    }

    public void setLoginTimeout(int seconds) throws SQLException {
        delegated.setLoginTimeout(seconds);
    }

    public int getLoginTimeout() throws SQLException {
        return delegated.getLoginTimeout();
    }

    public Logger getParentLogger() throws SQLFeatureNotSupportedException {
        return delegated.getParentLogger();
    }


    public <T> T unwrap(Class<T> iface) throws SQLException {
        return delegated.unwrap(iface);
    }

    public boolean isWrapperFor(Class<?> iface) throws SQLException {
        return delegated.isWrapperFor(iface);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        NamedDataSource that = (NamedDataSource) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name);
    }

    @Override
    public void close() throws IOException {
        if (delegated instanceof Closeable) {
            ((Closeable) delegated).close();
        }
    }

    @Override
    public String toString() {
        return "NamedDataSource{" +
                "name='" + name + '\'' +
                '}';
    }
}
