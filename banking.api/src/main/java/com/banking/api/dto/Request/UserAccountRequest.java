package com.banking.api.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserAccountRequest {
    private String accountName;
    private String accountPassword;
    private BigDecimal initialDeposit;
}
