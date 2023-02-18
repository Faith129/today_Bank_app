package com.banking.api.controller;


import com.banking.api.dto.Request.UserAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.models.UserAccountTransactions;
import com.banking.api.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {

    @Autowired
    private UserAccountService accountService;

    @PostMapping("/create_account")
    public ResponseEntity<List<ServiceResponse>> createAccount(@Valid @RequestBody UserAccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.createAccount(accountRequest));
    }

    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.transfer(request));
    }

    @GetMapping("balance/{accountNumber}")
    public ResponseEntity<ServiceResponse> getBalance(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getBalances(accountNumber));
    }
    @GetMapping("/transfer-history/{transactionId}")
    public ResponseEntity<?> getTransferHistoryFromTransaction(@PathVariable Long transactionId) {
        Optional<UserAccountTransactions> transferHistory = accountService.getTransferHistoryFromTransaction(transactionId);

        if (transferHistory.isPresent()) {
            return ResponseEntity.ok(transferHistory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
