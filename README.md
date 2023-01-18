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

