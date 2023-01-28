package org.coderclan.guidepost.datasource;

import java.util.List;

/**
 * Discovery Database Nodes.
 * <p>
 * It will use seed DataSource to get database nodes information, create Datasource for each node, determine whether if the node is readonly.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/14
 */
public interface DatabaseNodeDiscovery {

    /**
     * Set the Seed Datasource. {@link DatabaseNodeDiscovery#run()} will use the seed datasource to get a database connection. and use the connection to get Database Nodes information.
     *
     * @param seed
     */
    void setSeed(NamedDataSource seed);

    /**
     * Run the discovery to keep the database nodes information updated (periodically). The following things should be done in this method:
     * <ul><li>Getting nodes of database.</li>
     * <li>creating Datasource for each node.</li>
     * <li>     determining every node whether if it is readonly. </li></ul>
     */
    void run();

    /**
     * @return Writable DataSources
     */
    List<NamedDataSource> getWritableDataSources();

    /**
     * @return Read only DataSources
     */

    List<NamedDataSource> getReadOnlyDataSources();
}
