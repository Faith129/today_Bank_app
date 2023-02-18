package com.banking.api.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.math.BigDecimal;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "transactions")
public class UserAccountTransactions {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long transactionId;
    @Column(name = "transaction_date")
    private String transactionDate;
    @Column(name = "transaction_type")
    private String transactionType;
    @Column(name = "narration")
    private String narration;
    @Column(name = "amount")
    private BigDecimal amount;
    @Column(name = "account_balance")
    private BigDecimal accountBalance;
    @Column(name = "from_account_number")
    private String fromAccountNumber;
    @Column(name = "to_account_number")
    private String toAccountNumber;
    @ManyToOne
    @JoinColumn(name = "customer_id")
    private UserAccount account;
}
