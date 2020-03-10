package com.softuni.ebank.bindingModels;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class BankAccountBindingModel {
    private Long id;
    private String username;
    private String iban;
    private BigDecimal amount;
    private Long receiverId;

    public BankAccountBindingModel() {
    }

    public BankAccountBindingModel(Long id, String username, String iban, BigDecimal amount, Long receiverId) {
        this.id = id;
        this.username = username;
        this.iban = iban;
        this.amount = amount;
        this.receiverId = receiverId;
    }

    @NotNull
    @NotEmpty
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @NotNull
    @NotEmpty
    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    @NotNull
    @NotEmpty
    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }

    @NotNull
    @NotEmpty
    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    @NotNull
    @NotEmpty
    public Long getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(Long receiverId) {
        this.receiverId = receiverId;
    }
}
