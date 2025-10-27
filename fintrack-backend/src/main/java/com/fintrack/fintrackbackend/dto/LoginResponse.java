package com.fintrack.fintrackbackend.dto;

import com.fintrack.fintrackbackend.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class LoginResponse {

    private String token;
    private Integer userId;
    private String name;
    private List<Transaction> recentTransactions;
    private BigDecimal currentBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;

    public LoginResponse(String token,
                         Integer userId,
                         String name,
                         List<Transaction> recentTransactions,
                         BigDecimal currentBalance,
                         BigDecimal monthlyIncome,
                         BigDecimal monthlyExpense) {
        this.token = token;
        this.userId = userId;
        this.name = name;
        this.recentTransactions = recentTransactions;
        this.currentBalance = currentBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpense = monthlyExpense;
    }

    public String getToken() {
        return token;
    }

    public Integer getUserId() {
        return userId;
    }

    public String getName() {
        return name;
    }

    public List<Transaction> getRecentTransactions() {
        return recentTransactions;
    }

    public BigDecimal getCurrentBalance() {
        return currentBalance;
    }

    public BigDecimal getMonthlyIncome() {
        return monthlyIncome;
    }

    public BigDecimal getMonthlyExpense() {
        return monthlyExpense;
    }
}
