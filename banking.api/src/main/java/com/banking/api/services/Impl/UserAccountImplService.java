package com.banking.api.services.Impl;


import com.banking.api.dto.Request.UserAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.AccountInfoResponse;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.enums.TransactionType;
import com.banking.api.exception.ServiceException;
import com.banking.api.models.UserAccount;

import com.banking.api.models.UserAccountTransactions;
import com.banking.api.repository.TransactionRepository;
import com.banking.api.repository.UserAccountRepository;
import com.banking.api.services.UserAccountService;
import com.banking.api.utils.Applicationutils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.banking.api.enums.ResponseCode.*;
@Slf4j
@Service
@RequiredArgsConstructor
public class UserAccountImplService implements UserAccountService {

  private final UserAccountRepository userAccountRepository;
  private final TransactionRepository transactionRepository;


    @Override

//    public List<ServiceResponse> createAccount(UserAccountRequest accountRequest) {
//        List<ServiceResponse> response = new ArrayList<>();
//
//        if (userAccountRepository.existsByAccountName(accountRequest.getAccountName())) {
//            throw new ServiceException(Integer.valueOf(ALREADY_EXIST.getCanonicalCode()),
//                    "Account with name '" + accountRequest.getAccountName() + "' already exists",
//                    LocalDateTime.now().toString());
//        }
//
//        if (accountRequest.getInitialDeposit() == null
//                || accountRequest.getInitialDeposit().compareTo(new BigDecimal("500.00")) < 0) {
//            throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
//                    "Minimum initial deposit amount is #500", LocalDateTime.now().toString());
//        }
//
//        UserAccount account = UserAccount.builder().accountName(accountRequest.getAccountName())
//                .initialDeposit(accountRequest.getInitialDeposit())
//                .accountNumber(alwaysGetUniqueAccountNumber()).createDate(new Date()).transactions(new ArrayList<>())
//                .totalBalance(accountRequest.getInitialDeposit()).build();
//
//        try {
//            // Save the UserAccount object to the userAccountRepository
//            ServiceResponse response1 = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
//                    userAccountRepository.save(account));
//
//            // Create a new UserAccountTransaction object and set its fields
//            UserAccountTransactions transaction = UserAccountTransactions.builder()
//                    .transactionDate(new Date().toString())
//                    .narration("Initial deposit")
//                    .amount(accountRequest.getInitialDeposit())
//                    .transactionType(String.valueOf(TransactionType.DEPOSIT))
//                    .account(account)
//                    .build();
//
//            // Add the new UserAccountTransaction object to the transactions list of the UserAccount object
//            account.getTransactions().add(transaction);
//
//            // Save the UserAccount object with the updated transactions list to the userAccountRepository
//            userAccountRepository.save(account);
//
//            response.add(response1);
//        } catch (Exception e) {
//            log.error("Exception occurred while creating account {}", e.getMessage());
//        }
//
//        return response;
//    }
//

        public List<ServiceResponse> createAccount(UserAccountRequest accountRequest) {
        List<ServiceResponse> response = new ArrayList<>();

        if (userAccountRepository.existsByAccountName(accountRequest.getAccountName())) {
            throw new ServiceException(Integer.valueOf(ALREADY_EXIST.getCanonicalCode()),
                "Account with name '" + accountRequest.getAccountName() + "' already exists",
                LocalDateTime.now().toString());
        }

        if (accountRequest.getInitialDeposit() == null
            || accountRequest.getInitialDeposit().compareTo(new BigDecimal("500.00")) < 0) {
            throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
                "Minimum initial deposit amount is #500", LocalDateTime.now().toString());
        }

        UserAccount account = UserAccount.builder().accountName(accountRequest.getAccountName())
            .initialDeposit(accountRequest.getInitialDeposit())
            .accountNumber(alwaysGetUniqueAccountNumber()).createDate(new Date()).transactions(new ArrayList<>())
            .totalBalance(accountRequest.getInitialDeposit()).build();

        UserAccount account2 = UserAccount.builder().accountName(accountRequest.getAccountName())
                .initialDeposit(accountRequest.getInitialDeposit())
                .accountNumber(alwaysGetUniqueAccountNumber()).createDate(new Date()).transactions(new ArrayList<>())
                .totalBalance(accountRequest.getInitialDeposit()).build();

        UserAccount account3 = UserAccount.builder().accountName(accountRequest.getAccountName())
                .initialDeposit(accountRequest.getInitialDeposit())
                .accountNumber(alwaysGetUniqueAccountNumber()).createDate(new Date()).transactions(new ArrayList<>())
                .totalBalance(accountRequest.getInitialDeposit()).build();

        try {
            ServiceResponse response1 = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                userAccountRepository.save(account));

            ServiceResponse response2 = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                    userAccountRepository.save(account2));
            ServiceResponse response3 = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                    userAccountRepository.save(account3));
            response.add(response1);
            response.add(response2);
            response.add(response3);





        } catch (Exception e) {
            log.error("Exception occurred while creating account {}", e.getMessage());

        }

        return response;
    }
    private String alwaysGetUniqueAccountNumber() {
        String accountNumber = "";
        for (;;) {
            accountNumber = Applicationutils.generateUniqueAccountNumber();
            if (!userAccountRepository.existsByAccountNumber(accountNumber)) {
                break;
            }
        }
        return accountNumber;
    }


@Override
    public ServiceResponse transfer(TransactionRequest transferRequest) throws AccountNotFoundException {
        String fromAccountNumber = transferRequest.getFromAccountNumber();
        String toAccountNumber = transferRequest.getToAccountNumber();
        BigDecimal amount = transferRequest.getAmount();

        UserAccount fromAccount = userAccountRepository.findByAccountNumber(fromAccountNumber).orElseThrow(()->
                new AccountNotFoundException(" Account " + fromAccountNumber + " Not Found "));
        UserAccount toAccount = userAccountRepository.findByAccountNumber(toAccountNumber).orElseThrow(()->
                new AccountNotFoundException("Account" + toAccountNumber + " Does not found "));
        if(fromAccount.getTotalBalance().compareTo(BigDecimal.ONE) == 1
                && fromAccount.getTotalBalance().compareTo(amount) == 1
        ){
            fromAccount.setTotalBalance(fromAccount.getTotalBalance().subtract(amount));
            userAccountRepository.save(fromAccount);
            toAccount.setTotalBalance(toAccount.getTotalBalance().add(amount));
            userAccountRepository.save(toAccount);
            if (fromAccount.getTotalBalance().compareTo(transferRequest.getAmount())>0) {
                return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                        Applicationutils.SUCCESSFUL_TRANSFER);
            }

        }
    throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
            "Insufficient Fund",
            LocalDateTime.now().toString());
    }

@Override
    public ServiceResponse getBalances(String accountNumber) {
        return userAccountRepository.findByAccountNumber(accountNumber).map(account -> {
            AccountInfoResponse response = AccountInfoResponse.builder().totalBalance(account.getTotalBalance()).build();
            return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                    response);
        }).orElseThrow(() -> new ServiceException(Integer.valueOf(NOT_FOUND.getCanonicalCode()),
                NOT_FOUND.getDescription(), LocalDateTime.now().toString()));
    }
    @Override
    public Optional<UserAccountTransactions> getTransferHistoryFromTransaction(Long transactionId) {
        return transactionRepository.findByTransactionId(transactionId);
    }



}
