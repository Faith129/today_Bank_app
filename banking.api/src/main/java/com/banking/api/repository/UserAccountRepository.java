package com.banking.api.repository;

import com.banking.api.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findById(Long id);
    Optional<UserAccount> findByAccountNumber (String accountNumber);
    Boolean existsByAccountName(String accountName);
    Boolean existsByAccountNumber(String accountNumber);
}
