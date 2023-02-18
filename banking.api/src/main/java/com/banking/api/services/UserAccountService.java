package com.banking.api.services;

;
import com.banking.api.dto.Request.UserAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.models.UserAccountTransactions;

import javax.security.auth.login.AccountNotFoundException;
import java.util.List;
import java.util.Optional;

public interface UserAccountService {
    List<ServiceResponse> createAccount(UserAccountRequest accountRequest);
    ServiceResponse transfer(TransactionRequest transferRequest) throws AccountNotFoundException;
    ServiceResponse getBalances(String accountNumber);
    //ServiceResponse getTransferHistory(Long id);
  //  List<UserAccountTransactions> getTransferHistory(Long id);
    Optional<UserAccountTransactions> getTransferHistoryFromTransaction(Long transactionId);
}
