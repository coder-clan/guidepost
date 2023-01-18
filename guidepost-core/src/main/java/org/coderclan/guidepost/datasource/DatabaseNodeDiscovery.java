package org.coderclan.guidepost.datasource;

import javax.sql.DataSource;
import java.util.List;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/14
 */
public interface DatabaseNodeDiscovery {

    void setSeed( NamedDataSource seed);

    void run();

    List<NamedDataSource> getWritableDataSources();

    List<NamedDataSource> getReadOnlyDataSources();
}
