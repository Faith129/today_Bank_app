package com.banking.api.dto.Response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TransactionResponse {
    private String transactionType;
    private BigDecimal amount;
    private BigDecimal accountBalance;
    private String fromAccountNumber;
    private String toAccountNumber;
}
