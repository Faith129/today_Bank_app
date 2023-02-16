package com.banking.api.dto.Request;

import com.banking.api.models.BankAccount;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerRequest {
    private String accountName;
    private List<BankAccount> accounts;
}
