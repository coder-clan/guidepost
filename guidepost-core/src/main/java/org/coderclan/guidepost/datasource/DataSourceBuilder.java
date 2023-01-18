package org.coderclan.guidepost.datasource;

import javax.sql.DataSource;

/**
 * TODO change me.
 *
 * @author TODO
 * @date 2023/1/17
 */
public interface DataSourceBuilder {
    NamedDataSource create();

    NamedDataSource create(String address);
}
