package com.banking.api.services;

import com.banking.api.dto.Request.AccountRequest;
//import com.banking.api.dto.Request.BankAccountRequest;
//import com.banking.api.dto.Request.CustomerRequest;
import com.banking.api.dto.Request.BankAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.models.UserAccount;

import javax.security.auth.login.AccountNotFoundException;

public interface UserAccountService {
    ServiceResponse createAccount(AccountRequest accountRequest);
    ServiceResponse transfer(TransactionRequest transferRequest) throws AccountNotFoundException;
    ServiceResponse getBalance(String accountNumber);
    ServiceResponse getStatement(String accountNumber);
    ServiceResponse createCustomerWithAccounts(BankAccountRequest accountRequest);
    ServiceResponse getTransferHistory(Long id);
}
