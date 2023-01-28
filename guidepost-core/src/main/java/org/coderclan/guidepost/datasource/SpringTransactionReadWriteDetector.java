package org.coderclan.guidepost.datasource;

import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * ReadWriteDetector implemented with {@link TransactionSynchronizationManager#isCurrentTransactionReadOnly()}
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/28
 */
public class SpringTransactionReadWriteDetector implements ReadWriteDetector {
    @Override
    public boolean isCurrentTransactionReadOnly() {
        return TransactionSynchronizationManager.isCurrentTransactionReadOnly();
    }
}
