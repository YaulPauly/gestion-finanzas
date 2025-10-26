package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.GoalContributionRequest;
import com.fintrack.fintrackbackend.dto.GoalRequest;
import com.fintrack.fintrackbackend.model.Goal;
import com.fintrack.fintrackbackend.model.GoalStatus;
import org.springframework.data.domain.Page;

public interface GoalService {
    Goal createGoal(GoalRequest request, Integer userId);
    Page<Goal> getGoals(Integer userId, GoalStatus status, int page, int size);
    Goal contributeToGoal(Long goalId, Integer userId, GoalContributionRequest request);
    Goal archiveGoal(Long goalId, Integer userId);
    Goal getGoalById(Long goalId, Integer userId);
}
