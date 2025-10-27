package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.ExpenseRequest;
import com.fintrack.fintrackbackend.dto.IncomeRequest;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Crear ingreso
    @PostMapping("/income")
    public Transaction createIncome(@Valid @RequestBody IncomeRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("userId");
        return transactionService.createIncomeTransaction(request, userId);
    }

    //  Lista de ingresos
    @GetMapping("/income")
    public Page<Transaction> getAllIncomes(HttpServletRequest servletRequest,
                                           @RequestParam(defaultValue = "0") int page,
                                           @RequestParam(defaultValue = "10") int size) {
        Integer userId = (Integer) servletRequest.getAttribute("userId");
        return transactionService.getIncomesByUser(userId, page, size);
    }

    // Crear gasto
    @PostMapping("/expense")
    public Transaction createExpense(@Valid @RequestBody ExpenseRequest request, HttpServletRequest servletRequest) {
        Integer userId = (Integer) servletRequest.getAttribute("userId");
        return transactionService.createExpenseTransaction(request, userId);
    }

    // Lista de gastos
    @GetMapping("/expense")
    public Page<Transaction> getAllExpenses(HttpServletRequest servletRequest,
                                            @RequestParam(defaultValue = "0") int page,
                                            @RequestParam(defaultValue = "10") int size) {
        Integer userId = (Integer) servletRequest.getAttribute("userId");
        return transactionService.getExpensesByUser(userId, page, size);
    }
}
