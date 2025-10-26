package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.CategoryRequest;
import com.fintrack.fintrackbackend.model.Category;

import java.util.List;

public interface CategoryService {
    Category createCategory(CategoryRequest request, Integer userId);
    List<Category> getAllCategories(Integer userId);
    Category getCategoryById(Integer id, Integer userId);
    Category updateCategory(Integer id, CategoryRequest request, Integer userId);
    void deleteCategory(Integer id, Integer userId);
}
