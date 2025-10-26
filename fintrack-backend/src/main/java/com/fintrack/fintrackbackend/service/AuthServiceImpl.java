package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.LoginRequest;
import com.fintrack.fintrackbackend.dto.LoginResponse;
import com.fintrack.fintrackbackend.model.Transaction;
import com.fintrack.fintrackbackend.model.TransactionType;
import com.fintrack.fintrackbackend.model.User;
import com.fintrack.fintrackbackend.repository.UserRepository;
import com.fintrack.fintrackbackend.security.JwtUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.util.List;

@Service
public class AuthServiceImpl implements AuthService {

    private static final int RECENT_TRANSACTIONS_LIMIT = 4;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TransactionService transactionService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Usuario no encontrado"));

        if (!user.getPassword().equals(request.getPassword())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Credenciales inválidas");
        }

        String token = jwtUtils.generateToken(user.getId());
        List<Transaction> recentTransactions = transactionService.getLastTransactions(user.getId(), RECENT_TRANSACTIONS_LIMIT);
        BigDecimal currentBalance = transactionService.getCurrentBalance(user.getId());
        if (user.getCurrentBalance() == null || user.getCurrentBalance().compareTo(currentBalance) != 0) {
            user.setCurrentBalance(currentBalance);
            userRepository.save(user);
        }
        BigDecimal monthlyIncome = transactionService.getMonthlyTotal(user.getId(), TransactionType.INCOME);
        BigDecimal monthlyExpense = transactionService.getMonthlyTotal(user.getId(), TransactionType.EXPENSE);

        return new LoginResponse(
                token,
                user.getId(),
                user.getName(),
                recentTransactions,
                currentBalance,
                monthlyIncome,
                monthlyExpense
        );
    }
}
