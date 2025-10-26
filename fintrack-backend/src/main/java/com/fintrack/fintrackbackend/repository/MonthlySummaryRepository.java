package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.model.MonthlySummary;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MonthlySummaryRepository extends JpaRepository<MonthlySummary, Long> {
    Optional<MonthlySummary> findByUserIdAndMonthYear(Integer userId, String monthYear);
}
