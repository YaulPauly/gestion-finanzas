package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.GoalContributionRequest;
import com.fintrack.fintrackbackend.dto.GoalRequest;
import com.fintrack.fintrackbackend.model.Goal;
import com.fintrack.fintrackbackend.model.GoalStatus;
import com.fintrack.fintrackbackend.model.User;
import com.fintrack.fintrackbackend.repository.GoalRepository;
import com.fintrack.fintrackbackend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;

@Service
public class GoalServiceImpl implements GoalService {

    private static final int MAX_PAGE_SIZE = 50;

    @Autowired
    private GoalRepository goalRepository;

    @Autowired
    private UserRepository userRepository;

    @Override
    @Transactional
    public Goal createGoal(GoalRequest request, Integer userId) {
        Goal goal = new Goal();
        goal.setName(request.getName().trim());
        goal.setTarget(request.getTarget());
        goal.setDescription(request.getDescription());
        goal.setUserId(userId);
        goal.setSavedAmount(BigDecimal.ZERO);
        goal.setStatus(GoalStatus.IN_PROGRESS);
        return goalRepository.save(goal);
    }

    @Override
    public Page<Goal> getGoals(Integer userId, GoalStatus status, int page, int size) {
        Pageable pageable = buildPageable(page, size);
        if (status == null) {
            return goalRepository.findByUserId(userId, pageable);
        }
        return goalRepository.findByUserIdAndStatus(userId, status, pageable);
    }

    @Override
    public Goal getGoalById(Long goalId, Integer userId) {
        return goalRepository.findByIdAndUserId(goalId, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Meta no encontrada"));
    }

    @Override
    @Transactional
    public Goal contributeToGoal(Long goalId, Integer userId, GoalContributionRequest request) {
        Goal goal = getGoalById(goalId, userId);
        if (goal.getStatus() == GoalStatus.ARCHIVED) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "La meta está archivada.");
        }

        BigDecimal amount = request.getAmount();
        if (amount == null || amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "El monto debe ser mayor a 0.");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        BigDecimal currentBalance = user.getCurrentBalance() != null ? user.getCurrentBalance() : BigDecimal.ZERO;
        if (currentBalance.compareTo(amount) < 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Saldo insuficiente para realizar el aporte.");
        }

        user.setCurrentBalance(currentBalance.subtract(amount));
        BigDecimal newSavedAmount = goal.getSavedAmount().add(amount);
        goal.setSavedAmount(newSavedAmount);

        if (newSavedAmount.compareTo(goal.getTarget()) >= 0) {
            goal.setStatus(GoalStatus.ACHIEVED);
        } else {
            goal.setStatus(GoalStatus.IN_PROGRESS);
        }

        userRepository.save(user);
        return goalRepository.save(goal);
    }

    @Override
    @Transactional
    public Goal archiveGoal(Long goalId, Integer userId) {
        Goal goal = getGoalById(goalId, userId);
        if (goal.getStatus() == GoalStatus.ARCHIVED) {
            return goal;
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Usuario no encontrado"));

        BigDecimal refund = goal.getSavedAmount();
        if (refund.compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal currentBalance = user.getCurrentBalance() != null ? user.getCurrentBalance() : BigDecimal.ZERO;
            user.setCurrentBalance(currentBalance.add(refund));
            goal.setSavedAmount(BigDecimal.ZERO);
            userRepository.save(user);
        }

        goal.setStatus(GoalStatus.ARCHIVED);
        return goalRepository.save(goal);
    }

    private Pageable buildPageable(int page, int size) {
        int safePage = Math.max(page, 0);
        int safeSize = Math.min(Math.max(size, 1), MAX_PAGE_SIZE);
        return PageRequest.of(safePage, safeSize, Sort.by(Sort.Direction.DESC, "createdAt"));
    }
}
