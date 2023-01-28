package org.coderclan.guidepost.datasource;

/**
 * Detect the current Database Transaction readonly or not.
 * <p>
 * Implementations may need to invent a mechanism to mark current transaction is readonly or not.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/28
 */
public interface ReadWriteDetector {
    /**
     * @return true if current database Transaction is read only.
     */
    boolean isCurrentTransactionReadOnly();
}
