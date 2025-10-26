package com.fintrack.fintrackbackend.repository;

import com.fintrack.fintrackbackend.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CategoryRepository extends JpaRepository<Category, Integer> {
    List<Category> findByUserId(Integer userId);
    Optional<Category> findByIdAndUserId(Integer id, Integer userId);
    boolean existsByNameIgnoreCaseAndUserId(String name, Integer userId);
}
