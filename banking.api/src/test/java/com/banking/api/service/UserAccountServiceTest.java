package com.banking.api.service;

import com.banking.api.dto.Request.TransactionRequest;
import com.banking.api.dto.Response.ServiceResponse;
import com.banking.api.exception.ServiceException;
import com.banking.api.models.UserAccount;
import com.banking.api.repository.UserAccountRepository;
import com.banking.api.services.UserAccountService;
import com.banking.api.utils.Applicationutils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.security.auth.login.AccountNotFoundException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static com.banking.api.enums.ResponseCode.OK;
import static org.assertj.core.api.Fail.fail;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class UserAccountServiceTest {

    @InjectMocks
    private UserAccountService userAccountService;

    @Mock
    private UserAccountRepository userAccountRepository;

    @Test
    void testCreateAccountSuccess() {
        BankAccountRequest request = new BankAccountRequest("John Allen", new BigDecimal("1000.00"));
        UserAccount account = new UserAccount();
        account.setId(1L);
        account.setAccountName(request.getAccountName());
        account.setInitialDeposit(request.getInitialDeposit());
        account.setAccountNumber("1234567890");
        account.setCreateDate(new Date());
        account.setTransactions(new ArrayList<>());
        account.setTotalBalance(request.getInitialDeposit());

        when(userAccountRepository.existsByAccountName(request.getAccountName())).thenReturn(false);
        when(userAccountRepository.existsByAccountNumber(anyString())).thenReturn(false);
        when(userAccountRepository.save(any(UserAccount.class))).thenReturn(account);

        List<ServiceResponse> response = userAccountService.createAccount(request);

        assertEquals(3, response.size());
        assertEquals(200, response.get(0).getStatusCode());
        assertEquals(200, response.get(1).getStatusCode());
        assertEquals(200, response.get(2).getStatusCode());
    }

    @Test
    void testCreateAccountAlreadyExists() {
        BankAccountRequest request = new BankAccountRequest("John Allen", new BigDecimal("1000.00"));

        when(userAccountRepository.existsByAccountName(request.getAccountName())).thenReturn(true);

        try {
            userAccountService.createAccount(request);
            fail("Expected ServiceException was not thrown.");
        } catch (ServiceException e) {
            assertEquals(409, e.getHttpCode());
            assertEquals("Account with name 'John Allen' already exists", e.getMessage());
        }
    }

    @Test
    void testCreateAccountInsufficientDeposit() {
        BankAccountRequest request = new BankAccountRequest("John Allen", new BigDecimal("100.00"));

        try {
            userAccountService.createAccount(request);
            fail("Expected ServiceException was not thrown.");
        } catch (ServiceException e) {
            assertEquals(400, e.getHttpCode());
            assertEquals("Minimum initial deposit amount is #500", e.getMessage());
        }
    }

    @Test
    void testTransfer() throws AccountNotFoundException {
        // Set up test data
        String fromAccountNumber = "1234567890";
        String toAccountNumber = "0987654321";
        BigDecimal amount = new BigDecimal("50.00");
        UserAccount fromAccount = new UserAccount(fromAccountNumber, new BigDecimal("100.00"));
        UserAccount toAccount = new UserAccount(toAccountNumber, new BigDecimal("0.00"));
        when(userAccountRepository.findByAccountNumber(fromAccountNumber)).thenReturn(Optional.of(fromAccount));
        when(userAccountRepository.findByAccountNumber(toAccountNumber)).thenReturn(Optional.of(toAccount));

        // Make the transfer request
        TransactionRequest transferRequest = new TransactionRequest(fromAccountNumber, toAccountNumber, amount);
        ServiceResponse response = userAccountService.transfer(transferRequest);

        // Verify that the transfer was successful and account balances were updated
        assertEquals(OK.getCanonicalCode(), response.getStatusCode());
        assertEquals(Applicationutils.SUCCESSFUL_TRANSFER, response.getStatusMessage());
        assertEquals(fromAccount.getTotalBalance().subtract(amount), userAccountRepository.findByAccountNumber(fromAccountNumber).get().getTotalBalance());
        assertEquals(toAccount.getTotalBalance().add(amount), userAccountRepository.findByAccountNumber(toAccountNumber).get().getTotalBalance());
    }


}
