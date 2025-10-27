package com.fintrack.fintrackbackend.dto;

import com.fintrack.fintrackbackend.model.Transaction;

import java.math.BigDecimal;
import java.util.List;

public class DashboardSummaryResponse {

    private BigDecimal currentBalance;
    private BigDecimal monthlyIncome;
    private BigDecimal monthlyExpense;
    private List<Transaction> recentTransactions;

    public DashboardSummaryResponse(BigDecimal currentBalance,
                                    BigDecimal monthlyIncome,
                                    BigDecimal monthlyExpense,
                                    List<Transaction> recentTransactions) {
        this.currentBalance = currentBalance;
        this.monthlyIncome = monthlyIncome;
        this.monthlyExpense = monthlyExpense;
        this.recentTransactions = recentTransactions;
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

    public List<Transaction> getRecentTransactions() {
        return recentTransactions;
    }
}
