package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.GoalContributionRequest;
import com.fintrack.fintrackbackend.dto.GoalRequest;
import com.fintrack.fintrackbackend.model.Goal;
import com.fintrack.fintrackbackend.model.GoalStatus;
import com.fintrack.fintrackbackend.service.GoalService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/goals")
public class GoalController {

    @Autowired
    private GoalService goalService;

    @PostMapping
    public Goal createGoal(@Valid @RequestBody GoalRequest request, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return goalService.createGoal(request, userId);
    }

    @GetMapping
    public Page<Goal> getGoals(HttpServletRequest servletRequest,
                               @RequestParam(required = false) String status,
                               @RequestParam(defaultValue = "0") int page,
                               @RequestParam(defaultValue = "10") int size) {
        Integer userId = getUserId(servletRequest);
        GoalStatus goalStatus = parseStatus(status);
        return goalService.getGoals(userId, goalStatus, page, size);
    }

    @GetMapping("/{id}")
    public Goal getGoal(@PathVariable Long id, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return goalService.getGoalById(id, userId);
    }

    @PostMapping("/{id}/contributions")
    public Goal contributeToGoal(@PathVariable Long id,
                                 @Valid @RequestBody GoalContributionRequest request,
                                 HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return goalService.contributeToGoal(id, userId, request);
    }

    @PostMapping("/{id}/archive")
    public Goal archiveGoal(@PathVariable Long id, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return goalService.archiveGoal(id, userId);
    }

    private Integer getUserId(HttpServletRequest servletRequest) {
        Object value = servletRequest.getAttribute("userId");
        if (value instanceof Integer userId) {
            return userId;
        }
        throw new IllegalStateException("No se pudo determinar el usuario autenticado.");
    }

    private GoalStatus parseStatus(String status) {
        if (status == null || status.isBlank()) {
            return null;
        }
        try {
            return GoalStatus.valueOf(status.toUpperCase());
        } catch (IllegalArgumentException ex) {
            throw new IllegalArgumentException("Estado inválido. Valores permitidos: IN_PROGRESS, ACHIEVED, ARCHIVED.");
        }
    }
}
