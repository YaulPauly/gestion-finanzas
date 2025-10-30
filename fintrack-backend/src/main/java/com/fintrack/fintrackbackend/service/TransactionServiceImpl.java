package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.ExpenseRequest;
import com.fintrack.fintrackbackend.dto.IncomeRequest;
import com.fintrack.fintrackbackend.exception.InvalidOperationException;
import com.fintrack.fintrackbackend.exception.ResourceNotFoundException;
import com.fintrack.fintrackbackend.model.Category;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.model.User;
import com.fintrack.fintrackbackend.repository.CategoryRepository;
import com.fintrack.fintrackbackend.repository.GoalRepository;
import com.fintrack.fintrackbackend.repository.TransactionRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {

    @Autowired
    private TransactionRepository transactionRepository;

    @Autowired
    private MonthlySummaryService monthlySummaryService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    @Transactional
    public Transaction createIncomeTransaction(IncomeRequest request, Integer userId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.INCOME);
        transaction.setCategoryId(request.getCategoryId());
        transaction.setGoalId(null);
        transaction.setUserId(userId);
        Transaction saved = transactionRepository.save(transaction);
        monthlySummaryService.updateSummary(saved.getUserId(), saved.getDate(), saved.getType(), saved.getAmount());
        updateUserBalance(saved.getUserId(), saved.getAmount(), true);
        return saved;
    }

    @Override
    @Transactional
    public Transaction createExpenseTransaction(ExpenseRequest request, Integer userId) {
        Transaction transaction = new Transaction();
        transaction.setAmount(request.getAmount());
        transaction.setDescription(request.getDescription());
        transaction.setType(TransactionType.EXPENSE);
        transaction.setCategoryId(request.getCategoryId());
        transaction.setUserId(userId);
        Transaction saved = transactionRepository.save(transaction);
        monthlySummaryService.updateSummary(saved.getUserId(), saved.getDate(), saved.getType(), saved.getAmount());
        updateUserBalance(saved.getUserId(), saved.getAmount(), false);
        return saved;
    }

    @Override
    public Page<Transaction> getIncomesByUser(Integer userId, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        return transactionRepository.findByUserIdAndType(userId, TransactionType.INCOME, pageable);
    }

    @Override
    public Page<Transaction> getExpensesByUser(Integer userId, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        return transactionRepository.findByUserIdAndType(userId, TransactionType.EXPENSE, pageable);
    }

    @Override
    public List<Transaction> getLastTransactions(Integer userId, int limit) {
        return transactionRepository.findByUserIdOrderByCreatedAtDesc(userId, PageRequest.of(0, limit));
    }

    @Override
    public Transaction getTransactionById(Long id,Integer userId) {
        return transactionRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Transacción no encontrada con id: " + id));
    }

    @Override
    @Transactional
    public Transaction updateExpense(Long id, ExpenseRequest request, Integer userId) {
        // 1. Buscar la transacción existente
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Gasto no encontrado con id: " + id));

        // 2. Validar que sea un Gasto (si tu API maneja Incomes y Expenses juntas)
        if (existingTransaction.getType() != TransactionType.EXPENSE) {
            throw new InvalidOperationException("La transacción con id " + id + " no es un Gasto.");
        }

        // 3. Actualizar campos
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setDescription(request.getDescription());

        // Actualizar Categoría
        Category newCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));

        existingTransaction.setCategoryId(request.getCategoryId());

        // 4. Guardar y retornar
        return transactionRepository.save(existingTransaction);
    }

    @Override
    @Transactional
    public Transaction updateIncome(Long id, IncomeRequest request,Integer userId) {
        Transaction existingTransaction = transactionRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Ingreso no encontrado con id: " + id));
        // 1. Validar que sea un Ingreso (INCOME)
        if (existingTransaction.getType() != TransactionType.INCOME) {
            throw new InvalidOperationException("La transacción con id " + id + " no es un Ingreso.");
        }
        // 2. Actualizar campos (Monto y Descripción)
        existingTransaction.setAmount(request.getAmount());
        existingTransaction.setDescription(request.getDescription());
        // 3. Actualizar Categoría
        Category newCategory = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Categoría no encontrada."));

        existingTransaction.setCategoryId(request.getCategoryId());
        // 4. Guardar y retornar
        return transactionRepository.save(existingTransaction);
    }

    @Override
    public BigDecimal getMonthlyTotal(Integer userId, TransactionType type) {
        YearMonth currentMonth = YearMonth.now();
        LocalDate start = currentMonth.atDay(1);
        LocalDate end = currentMonth.atEndOfMonth();
        return transactionRepository.sumByUserIdAndTypeBetweenDates(userId, type, start, end);
    }

    @Override
    public BigDecimal getTotalByType(Integer userId, TransactionType type) {
        return transactionRepository.sumByUserIdAndType(userId, type);
    }

    @Override
    public BigDecimal getCurrentBalance(Integer userId) {
        BigDecimal totalIncome = getTotalByType(userId, TransactionType.INCOME);
        BigDecimal totalExpense = getTotalByType(userId, TransactionType.EXPENSE);
        BigDecimal reservedSavings = goalRepository.sumSavedAmountByUser(userId);
        return totalIncome.subtract(totalExpense).subtract(reservedSavings);
    }

    private void updateUserBalance(Integer userId, BigDecimal amount, boolean isIncome) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        BigDecimal currentBalance = user.getCurrentBalance() != null ? user.getCurrentBalance() : BigDecimal.ZERO;
        BigDecimal newBalance = isIncome ? currentBalance.add(amount) : currentBalance.subtract(amount);
        user.setCurrentBalance(newBalance);
        userRepository.save(user);
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), 50);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "date", "id"));
    }
}
