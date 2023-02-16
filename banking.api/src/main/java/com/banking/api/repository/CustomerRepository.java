package com.banking.api.repository;

import com.banking.api.models.Customer;
import com.banking.api.models.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByAccountName (String accountName);
    Boolean existsByAccountName(String accountName);

}
