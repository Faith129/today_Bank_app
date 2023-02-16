package com.banking.api.repository;

import com.banking.api.models.UserAccount;
import com.banking.api.models.UserAccountTransactions;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface TransactionRepository extends JpaRepository<UserAccountTransactions, Long> {
    Optional<UserAccountTransactions> findByfromAccountNumber(UserAccount account);

}
