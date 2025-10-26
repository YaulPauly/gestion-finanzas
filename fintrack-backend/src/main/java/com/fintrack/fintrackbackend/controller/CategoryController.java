package com.fintrack.fintrackbackend.controller;

import com.fintrack.fintrackbackend.dto.CategoryRequest;
import com.fintrack.fintrackbackend.model.Category;
import com.fintrack.fintrackbackend.service.CategoryService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @PostMapping
    public Category createCategory(@Valid @RequestBody CategoryRequest request, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return categoryService.createCategory(request, userId);
    }

    @GetMapping
    public List<Category> getAllCategories(HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return categoryService.getAllCategories(userId);
    }

    @GetMapping("/{id}")
    public Category getCategory(@PathVariable Integer id, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return categoryService.getCategoryById(id, userId);
    }

    @PutMapping("/{id}")
    public Category updateCategory(@PathVariable Integer id,
                                   @Valid @RequestBody CategoryRequest request,
                                   HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        return categoryService.updateCategory(id, request, userId);
    }

    @DeleteMapping("/{id}")
    public void deleteCategory(@PathVariable Integer id, HttpServletRequest servletRequest) {
        Integer userId = getUserId(servletRequest);
        categoryService.deleteCategory(id, userId);
    }

    private Integer getUserId(HttpServletRequest servletRequest) {
        Object value = servletRequest.getAttribute("userId");
        if (value instanceof Integer userId) {
            return userId;
        }
        throw new IllegalStateException("No se pudo determinar el usuario autenticado.");
    }
}
