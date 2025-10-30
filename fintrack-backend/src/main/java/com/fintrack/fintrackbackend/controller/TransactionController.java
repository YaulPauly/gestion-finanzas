package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.ExpenseRequest;
import com.fintrack.fintrackbackend.dto.IncomeRequest;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/api/transactions")
public class TransactionController {

    @Autowired
    private TransactionService transactionService;

    // Método auxiliar para obtener el userId del contexto de seguridad.
    private Integer getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        // Asume que el ID del usuario (Integer) fue puesto en el Principal durante la autenticación JWT
        if (authentication != null && authentication.getPrincipal() instanceof Integer) {
            return (Integer) authentication.getPrincipal();
        }
        throw new IllegalStateException("ID de usuario no encontrado en el contexto de seguridad.");
    }

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

    // Actualizar gastos
    @PutMapping("/expense/{id}")
    public ResponseEntity<Transaction> updateExpense(
            @PathVariable Long id,
            @Valid @RequestBody ExpenseRequest request) { // Asegúrate de validar el cuerpo

        Integer userId = getCurrentUserId(); // 1. Obtener userId
        Transaction updatedTransaction = transactionService.updateExpense(id, request, userId);

        return ResponseEntity.ok(updatedTransaction);
    }

    // Actualizar ingresos
    @PutMapping("/income/{id}")
    public ResponseEntity<Transaction> updateIncome(
            @PathVariable Long id,
            @Valid @RequestBody IncomeRequest request) {

        Integer userId = getCurrentUserId();
        Transaction updatedTransaction = transactionService.updateIncome(id, request, userId);
        return ResponseEntity.ok(updatedTransaction);
    }

    // Obtener los detalles de una transacción por ID.
    @GetMapping("/{id}")
    public ResponseEntity<Transaction> getTransactionDetails(@PathVariable Long id) {
        Integer userId = getCurrentUserId();
        Transaction transaction = transactionService.getTransactionById(id, userId);
        return ResponseEntity.ok(transaction);
    }
}
