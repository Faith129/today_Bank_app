package com.banking.api.services.Impl;

import com.banking.api.dto.Request.AccountRequest;
//import com.banking.api.dto.Request.BankAccountRequest;
//import com.banking.api.dto.Request.CustomerRequest;
import com.banking.api.dto.Request.BankAccountRequest;
import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.AccountInfoResponse;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.exception.ServiceException;
import com.banking.api.models.BankAccount;
import com.banking.api.models.Customer;
import com.banking.api.models.UserAccount;
import com.banking.api.models.UserAccountTransactions;
import com.banking.api.repository.CustomerRepository;
import com.banking.api.repository.TransactionRepository;
import com.banking.api.repository.UserAccountRepository;
import com.banking.api.services.UserAccountService;
import com.banking.api.utils.Applicationutils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

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
    private final CustomerRepository customerRepository;


    @Override
    public ServiceResponse createAccount(AccountRequest accountRequest) {
        ServiceResponse response;

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

        try {
            response = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                userAccountRepository.save(account));
        } catch (Exception e) {
            log.error("Exception occurred while creating account {}", e.getMessage());
            return new ServiceResponse(INTERNAL_SERVER_ERROR.getCanonicalCode(), INTERNAL_SERVER_ERROR.getDescription(),
                LocalDateTime.now().toString(), null);
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

//  //  private final CustomerRepository customerRepository;
//    @Override
//    public ServiceResponse createAccount(AccountRequest accountRequest) {
//        ServiceResponse response;
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
//            response = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
//                    userAccountRepository.save(account));
//        } catch (Exception e) {
//            log.error("Exception occurred while creating account {}", e.getMessage());
//            return new ServiceResponse(INTERNAL_SERVER_ERROR.getCanonicalCode(), INTERNAL_SERVER_ERROR.getDescription(),
//                    LocalDateTime.now().toString(), null);
//        }
//
//        return response;
//    }
//    private String alwaysGetUniqueAccountNumber() {
//        String accountNumber = "";
//        for (;;) {
//            accountNumber = Applicationutils.generateUniqueAccountNumber();
//            if (!userAccountRepository.existsByAccountNumber(accountNumber)) {
//                break;
//            }
//        }
//        return accountNumber;
//    }
//
//
    @Override
    public ServiceResponse createCustomerWithAccounts(BankAccountRequest accountRequest) {
        ServiceResponse response;
        // Check if customer with the same name already exists
        if (customerRepository.existsByAccountName(accountRequest.getAccountName())) {
            throw new ServiceException(Integer.valueOf(ALREADY_EXIST.getCanonicalCode()),
                "Customer with name '" + accountRequest.getAccountName() + "' already exists",
                LocalDateTime.now().toString());
        }

// Create the customer object
        Customer customer = Customer.builder()
            .accountName(accountRequest.getAccountName())
            .createDate(new Date())
            .accounts(new ArrayList<>())
            .build();
        // Create the accounts for the customer
        List<BankAccount> accounts = new ArrayList<>();
        for (BankAccount accountRequest1 : customer.getAccounts()) {

            // Check if account with the same name already exists
            if (userAccountRepository.existsByAccountName(accountRequest1.getAccountName())) {
                throw new ServiceException(Integer.valueOf(ALREADY_EXIST.getCanonicalCode()),
                    "Account with name '" + accountRequest1.getCustomer().getAccountName() + "' already exists",
                    LocalDateTime.now().toString());
            }

            // Check minimum deposit amount
            if (accountRequest.getInitialDeposit() == null
                || accountRequest.getInitialDeposit().compareTo(new BigDecimal("500.00")) < 0) {
                throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
                    "Minimum initial deposit amount is #500", LocalDateTime.now().toString());
            }

            // Create the account object
            BankAccount account = BankAccount.builder()
                .accountName(accountRequest1.getCustomer().getAccountName())
                .initialDeposit(accountRequest.getInitialDeposit())
                .accountNumber(alwaysGetUniqueAccountNumber())
                .createDate(new Date())
                .transactions(new ArrayList<>())
                .totalBalance(accountRequest.getInitialDeposit())
                .customer(customer)
                .build();
            accounts.add(account);
        }

        // Associate the accounts with the customer
        customer.setAccounts(accounts);

        try {
            response = new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                customerRepository.save(customer));
        } catch (Exception e) {
            log.error("Exception occurred while creating customer with accounts {}", e.getMessage());
            return new ServiceResponse(INTERNAL_SERVER_ERROR.getCanonicalCode(), INTERNAL_SERVER_ERROR.getDescription(),
                LocalDateTime.now().toString(), null);
        }

        return response;
    }

//
//
//
//    public ServiceResponse deposit(TransactionRequest request) {
//        return userAccountRepository.findByAccountNumber(request.getAccountNumber()).map(user -> {
//            if (request.getAmount().compareTo(new BigDecimal("1.00")) <= 0) {
//                throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
//                        "Minimum deposit amount is #1.00", LocalDateTime.now().toString());
//            }
//
//            if (request.getAmount() == null || request.getAmount().compareTo(new BigDecimal("1000000")) > 0) {
//                throw new ServiceException(Integer.valueOf(BAD_REQUEST.getCanonicalCode()),
//                        "Maximum deposit amount is #1,000,000", LocalDateTime.now().toString());
//            }
//
//            user.setTotalBalance(user.getTotalBalance().add(request.getAmount()));
//            user.getTransactions()
//                    .add(UserAccountTransactions.builder().amount(request.getAmount()).narration(request.getNarration())
//                            .transactionType("Credit").transactionDate(formatter.format(new Date()))
//                            .accountBalance(user.getTotalBalance()).build());
//            accountRepository.update(user);
//            return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
//                    Applicationutils.SUCCESSFULL_DEPOSIT);
//        }).orElseThrow(() -> new ServiceException(Integer.valueOf(NOT_FOUND.getCanonicalCode()),
//                NOT_FOUND.getDescription(), LocalDateTime.now().toString()));
//    }
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
//            Transactions transaction = transRepo.save(new Transactions(fromAccountNumber,amount, LocalDateTime.now().toString()));
//            return transaction;.compareTo(transfer.getAmount()) < 0
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
    public ServiceResponse getBalance(String accountNumber) {
        return userAccountRepository.findByAccountNumber(accountNumber).map(account -> {
            return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                    account.getTransactions());
        }).orElseThrow(() -> new ServiceException(Integer.valueOf(NOT_FOUND.getCanonicalCode()),
                NOT_FOUND.getDescription(), LocalDateTime.now().toString()));
    }

@Override
    public ServiceResponse getStatement(String accountNumber) {
        return userAccountRepository.findByAccountNumber(accountNumber).map(account -> {
            AccountInfoResponse response = AccountInfoResponse.builder().accountName(account.getAccountName())
                    .accountNumber(account.getAccountNumber()).totalBalance(account.getTotalBalance()).build();
            return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
                    response);
        }).orElseThrow(() -> new ServiceException(Integer.valueOf(NOT_FOUND.getCanonicalCode()),
                NOT_FOUND.getDescription(), LocalDateTime.now().toString()));
    }


    @Override
    public ServiceResponse getTransferHistory(Long id) {
        UserAccount userAccount = userAccountRepository.findById(id).orElseThrow(() ->
            new ResponseStatusException(HttpStatus.NOT_FOUND, "Account not found"));
        Optional<UserAccountTransactions> transfers = transactionRepository.findByfromAccountNumber(userAccount);
        return new ServiceResponse(OK.getCanonicalCode(), OK.getDescription(), LocalDateTime.now().toString(),
            transfers);

    }


}
