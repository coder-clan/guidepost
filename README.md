# Guidepost

The Guidepost is a solution of <code>Database Read Write Splitting</code>. It only
supports MySQL with <a href="https://dev.mysql.com/doc/refman/8.0/en/group-replication.html">Group replication</a> right
now. (MySQLInnoDB Cluster is supported since it is using Group Replication.) It does the following works.

- Proxying DataSource

The Guidepost creates Pooled Data Source of each available database server, and wrap them into a new Data Source which
named GuidepostDataSource. When GuidepostDataSource.getConnection() is called, it determines whether current Transaction
is readonly or not, then use a DataSource of a readonly server to get a database connection if current Transaction is
readonly, otherwise it used one of the DataSources of writable servers.

- Database Topology Discovery.

The Guidepost retrieve database servers information periodically. It will create DataSource of newly added server
automatically. It will also discard the DataSources of removed/offline database servers. And it will periodically check
the servers whether they are readonly or writable.

# How to Use

Please check the demo: <a href="tree/master/guidepost-demo">guidepost-demo</a>

- Depend on the Whistle in maven pom.xml.

<pre>
        &lt;dependency>
            &lt;groupId>org.coderclan&lt;/groupId>
            &lt;artifactId>spring-boot-starter-guidepost&lt;/artifactId>
        &lt;/dependency>
</pre>

- Configuration a dataSource as What <code>org.springframework.boot.autoconfigure.jdbc.DataSourceProperties</code>
  requires. This dataSource will be used as seed dataSource to get discovery Database Topology. Please check <code>
  spring.datasource.*</code> in <a href="blob/master/guidepost-demo/src/main/resources/application.yml">
  guidepost-demo/src/main/resources/application.yml</a>
- Grant database user SELECT privilege on
  table <a href="https://dev.mysql.com/doc/refman/8.0/en/group-replication-replication-group-members.html">
  performance_schema.replication_group_members</a>. (SQL: GRANT SELECT ON TABLE
  performance_schema.replication_group_members TO 'test'@'%')

# Limitation

- Autocommit of GuidepostDataSource will always be set to false.
- Property hibernate.connection.handling_mode need to be set to <a href="https://docs.jboss.org/hibernate/stable/orm/javadocs/org/hibernate/resource/jdbc/spi/PhysicalConnectionHandlingMode.html">DELAYED_ACQUISITION_*</a>. Guidepost set it
  to <code>DELAYED_ACQUISITION_AND_RELEASE_AFTER_TRANSACTION</code> default.

(If the two above requirements didn't meet, org.springframework.transaction.support.TransactionSynchronizationManager.isCurrentTransactionReadOnly will return false always. )