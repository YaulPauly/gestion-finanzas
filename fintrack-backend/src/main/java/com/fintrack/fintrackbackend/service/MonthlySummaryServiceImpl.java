package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.model.MonthlySummary;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.repository.MonthlySummaryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
public class MonthlySummaryServiceImpl implements MonthlySummaryService {

    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM");

    @Autowired
    private MonthlySummaryRepository monthlySummaryRepository;

    @Override
    @Transactional
    public void updateSummary(Integer userId, LocalDate transactionDate, TransactionType type, BigDecimal amount) {
        if (transactionDate == null || amount == null) {
            return;
        }

        String monthKey = MONTH_FORMATTER.format(transactionDate);

        MonthlySummary summary = monthlySummaryRepository
                .findByUserIdAndMonthYear(userId, monthKey)
                .orElseGet(() -> {
                    MonthlySummary newSummary = new MonthlySummary();
                    newSummary.setUserId(userId);
                    newSummary.setMonthYear(monthKey);
                    newSummary.setTotalIncome(BigDecimal.ZERO);
                    newSummary.setTotalExpense(BigDecimal.ZERO);
                    return newSummary;
                });

        if (type == TransactionType.INCOME) {
            summary.setTotalIncome(summary.getTotalIncome().add(amount));
        } else if (type == TransactionType.EXPENSE) {
            summary.setTotalExpense(summary.getTotalExpense().add(amount));
        }

        monthlySummaryRepository.save(summary);
    }
}
