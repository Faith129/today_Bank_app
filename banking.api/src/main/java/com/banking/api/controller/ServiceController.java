package com.banking.api.controller;

import com.banking.api.dto.Request.AccountRequest;
//import com.banking.api.dto.Request.BankAccountRequest;
//import com.banking.api.dto.Request.CustomerRequest;
import com.banking.api.dto.Request.BankAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.ServiceResponse;
//import com.banking.api.models.Customer;
import com.banking.api.services.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.security.auth.login.AccountNotFoundException;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ServiceController {

    @Autowired
    private UserAccountService accountService;

    @PostMapping("/create_account")
    public ResponseEntity<ServiceResponse> createAccount(@Valid @RequestBody BankAccountRequest accountRequest) {
        return ResponseEntity.ok(accountService.createCustomerWithAccounts(accountRequest));
    }


    @PostMapping("/transfer")
    public ResponseEntity<?> transfer(@Valid @RequestBody TransactionRequest request) throws AccountNotFoundException {
        return ResponseEntity.ok(accountService.transfer(request));
    }

//    @GetMapping("account_balance/{accountNumber}")
//    public ResponseEntity<ServiceResponse> getAccountStatement(@PathVariable String accountNumber) {
//        return ResponseEntity.ok(accountService.getBalance(accountNumber));
//    }

    @GetMapping("account_balance/{accountNumber}")
    public ResponseEntity<ServiceResponse> getAccountInfo(@PathVariable String accountNumber) {
        return ResponseEntity.ok(accountService.getStatement(accountNumber));
    }

    @GetMapping("transfer_history/{id}")
    public ResponseEntity<ServiceResponse> getTransferHistory(@PathVariable Long id) {
        return ResponseEntity.ok(accountService.getTransferHistory(id));
    }
}
