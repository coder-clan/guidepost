package org.coderclan.guidepost.demo.jpa;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionSynchronizationManager;

/**
 * TODO change me.
 *
 * @author aray(dot)chou(dot)cn(at)gmail(dot)com
 * @date 2023/1/16
 */
@Component
public class JpaTest {
    @Autowired
    private UserRepository repository;

    @Transactional(readOnly = false)
    public void createNewUser() {
        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        UserPo user = new UserPo();
        user.setId(100);
        user.setName("Aray");
        repository.save(user);
    }

    @Transactional(readOnly = true)
    public void getUser() {

        boolean readOnly = TransactionSynchronizationManager.isCurrentTransactionReadOnly();
        repository.findById(100).ifPresent(System.out::println);
    }
}
