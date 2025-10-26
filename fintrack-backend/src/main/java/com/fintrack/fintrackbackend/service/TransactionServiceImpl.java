package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.ExpenseRequest;
import com.fintrack.fintrackbackend.dto.IncomeRequest;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.model.User;
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
