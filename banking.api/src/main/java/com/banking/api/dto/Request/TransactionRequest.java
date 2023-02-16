package com.banking.api.dto.Request;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class TransactionRequest {
    @NotNull(message = "account number cannot be null")
    @NotBlank(message = "account number must be provided")
    private String accountNumber;
    private BigDecimal amount;
    private String narration = "";
    private String fromAccountNumber;
    private String toAccountNumber;
}
