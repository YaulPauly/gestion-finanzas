package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.DashboardSummaryResponse;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.service.TransactionService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
public class DashboardController {

    private static final int RECENT_TRANSACTIONS_LIMIT = 4;

    @Autowired
    private TransactionService transactionService;

    @GetMapping("/summary")
    public ResponseEntity<DashboardSummaryResponse> getDashboardSummary(HttpServletRequest request) {
        Integer userId = getUserId(request);

        BigDecimal currentBalance = transactionService.getCurrentBalance(userId);
        BigDecimal monthlyIncome = transactionService.getMonthlyTotal(userId, TransactionType.INCOME);
        BigDecimal monthlyExpense = transactionService.getMonthlyTotal(userId, TransactionType.EXPENSE);
        List<Transaction> recentTransactions = transactionService.getLastTransactions(userId, RECENT_TRANSACTIONS_LIMIT);

        DashboardSummaryResponse response = new DashboardSummaryResponse(
                currentBalance,
                monthlyIncome,
                monthlyExpense,
                recentTransactions
        );
        return ResponseEntity.ok(response);
    }

    private Integer getUserId(HttpServletRequest request) {
        Object attr = request.getAttribute("userId");
        if (attr instanceof Integer userId) {
            return userId;
        }
        throw new IllegalStateException("No se pudo determinar el usuario autenticado.");
    }
}
