package org.coderclan.guidepost.datasource;

/**
 * A Builder for creating Datasource.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/17
 */
public interface DataSourceBuilder {
    /**
     * Create a default datasource, which is often used as a seed datasource to discovery database nodes;
     *
     * @return a newly created Datasource.
     */
    NamedDataSource create();

    /**
     * Create a datasource of a database node.
     * @param address the Address of the database node. It is the host and port part of JDBC URL. e.g.  <code>centos-61:3306/</code> in jdbc:mysql://<code>centos-61:3306/</code>test
     * @return a newly created Datasource of database node <code>address</code>
     */
    NamedDataSource create(String address);
}
