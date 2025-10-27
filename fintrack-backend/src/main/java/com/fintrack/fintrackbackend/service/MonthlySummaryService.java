package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.model.TransactionType;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface MonthlySummaryService {
    void updateSummary(Integer userId, LocalDate transactionDate, TransactionType type, BigDecimal amount);
}
