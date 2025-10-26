package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.model.Goal;
import com.fintrack.fintrackbackend.model.GoalStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Optional;

public interface GoalRepository extends JpaRepository<Goal, Long> {
    Page<Goal> findByUserId(Integer userId, Pageable pageable);
    Page<Goal> findByUserIdAndStatus(Integer userId, GoalStatus status, Pageable pageable);
    Optional<Goal> findByIdAndUserId(Long id, Integer userId);

    @Query("SELECT COALESCE(SUM(g.savedAmount), 0) FROM Goal g WHERE g.userId = :userId AND g.status <> com.fintrack.fintrackbackend.model.GoalStatus.ARCHIVED")
    BigDecimal sumSavedAmountByUser(@Param("userId") Integer userId);
}
