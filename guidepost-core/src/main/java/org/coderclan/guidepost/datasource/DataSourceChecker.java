package org.coderclan.guidepost.datasource;

import javax.sql.DataSource;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/13
 */
public interface DataSourceChecker {
    default boolean isWritable(DataSource dataSource) {
        return !this.isReadonly(dataSource);
    }

    boolean isReadonly(DataSource dataSource);
}
