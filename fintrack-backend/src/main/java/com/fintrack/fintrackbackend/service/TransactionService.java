package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.ExpenseRequest;
import com.fintrack.fintrackbackend.dto.IncomeRequest;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;

import org.springframework.data.domain.Page;

import java.math.BigDecimal;
import java.util.List;

public interface TransactionService {
    Transaction createIncomeTransaction(IncomeRequest request, Integer userId);
    Transaction createExpenseTransaction(ExpenseRequest request, Integer userId);
    Page<Transaction> getIncomesByUser(Integer userId, int page, int size);
    Page<Transaction> getExpensesByUser(Integer userId, int page, int size);
    List<Transaction> getLastTransactions(Integer userId, int limit);
    BigDecimal getMonthlyTotal(Integer userId, TransactionType type);
    BigDecimal getTotalByType(Integer userId, TransactionType type);
    BigDecimal getCurrentBalance(Integer userId);
}
