package org.coderclan.guidepost.datasource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;

/**
 * <p>
 * A Read Write Splitting Datasource. It determines whether the current database transaction is readonly or writable.
 * If the current transaction is readonly, it will delegate all methods calling to a readonly datasource, otherwise it will delegate all
 * methods calling to a writable datasource.
 * </p>
 * <p>It uses {@link ReadWriteDetector } to detect whether the current database transaction is readonly or writeable.</p>
 * <p>It uses {@link DatabaseNodeDiscovery#getWritableDataSources()} and {@link DatabaseNodeDiscovery#getReadOnlyDataSources()} to get underlying datasources.</p>
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/5
 */
public class GuidePostDataSource extends AbstractDataSourceWrapper {
    private static Logger log = LoggerFactory.getLogger(GuidePostDataSource.class);
    private DataSourceChecker dataSourceChecker;

    @Autowired
    private DatabaseNodeDiscovery discovery;

    @Autowired
    private ReadWriteDetector readWriteDetector;
    @Autowired
    private DataSourceBuilder dataSourceBuilder;

    @PostConstruct
    public void init() {
        NamedDataSource seedDataSource = this.dataSourceBuilder.create();

        this.discovery.setSeed(seedDataSource);
        this.discovery.run();
    }

    @Override
    protected DataSource getDatasource() {
        boolean readOnly = this.readWriteDetector.isCurrentTransactionReadOnly();
        log.debug("isCurrentTransactionReadOnly: {}", readOnly);

        List<NamedDataSource> candidates = null;
        if (readOnly) {
            candidates = discovery.getReadOnlyDataSources();
            if (Objects.isNull(candidates) || candidates.isEmpty()) {
                log.warn("There is no underlying readonly DataSources for readonly Transaction, use WRITABLE DataSources instead.");
                candidates = discovery.getWritableDataSources();
            }
        } else {
            candidates = discovery.getWritableDataSources();
        }

        if (Objects.isNull(candidates) || candidates.isEmpty()) {
            throw new RuntimeException("There is no underlying DataSources.");
        }

        if (candidates.size() == 1) {
            return candidates.get(0);
        }

        int random = ThreadLocalRandom.current().nextInt(candidates.size());
        return candidates.get(random);
    }

}