package com.banking.api.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.Pattern;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "account")
public class UserAccount {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id")
    private Long id;
    @Column(name = "account_name")
    private String accountName;
    @Column(name = "account_number")
    @Pattern(regexp = "(\\+)?[0-9]{10}$", message = "account number must be between 10 digits")
    private String accountNumber;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Column(name = "password")
    private String accountPassword;
    @Column(name = "initial_deposit")
    private BigDecimal initialDeposit;
    @Column(name = "total_balance")
    private BigDecimal totalBalance;
    @Column(name = "create_date")
    private Date createDate;
    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "trans_id", referencedColumnName = "id")
    private List<UserAccountTransactions> transactions = new ArrayList<>();
}
