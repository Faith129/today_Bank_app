package com.banking.api.dto.Request;

import com.banking.api.models.Customer;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


import java.math.BigDecimal;
import java.util.Date;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BankAccountRequest {
    private String accountName;
    private BigDecimal initialDeposit;



}
