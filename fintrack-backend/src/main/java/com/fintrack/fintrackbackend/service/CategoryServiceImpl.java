package com.fintrack.fintrackbackend.service;

import com.fintrack.fintrackbackend.dto.CategoryRequest;
import com.fintrack.fintrackbackend.model.Category;
import com.fintrack.fintrackbackend.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Override
    public Category createCategory(CategoryRequest request, Integer userId) {
        validateUniqueName(request.getName(), userId);
        Category category = new Category();
        category.setName(request.getName().trim());
        category.setUserId(userId);
        return categoryRepository.save(category);
    }

    @Override
    public List<Category> getAllCategories(Integer userId) {
        return categoryRepository.findByUserId(userId);
    }

    @Override
    public Category getCategoryById(Integer id, Integer userId) {
        return categoryRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Categoría no encontrada"));
    }

    @Override
    public Category updateCategory(Integer id, CategoryRequest request, Integer userId) {
        Category category = getCategoryById(id, userId);
        String newName = request.getName().trim();
        if (!category.getName().equalsIgnoreCase(newName)) {
            validateUniqueName(newName, userId);
        }
        category.setName(newName);
        return categoryRepository.save(category);
    }

    @Override
    public void deleteCategory(Integer id, Integer userId) {
        Category category = getCategoryById(id, userId);
        try {
            categoryRepository.delete(category);
        } catch (DataIntegrityViolationException e) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "No se puede eliminar la categoría porque tiene transacciones asociadas.");
        }
    }

    private void validateUniqueName(String name, Integer userId) {
        if (categoryRepository.existsByNameIgnoreCaseAndUserId(name.trim(), userId)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Ya existe una categoría con ese nombre.");
        }
    }
}
