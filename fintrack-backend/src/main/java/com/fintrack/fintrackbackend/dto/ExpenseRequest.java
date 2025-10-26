package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class ExpenseRequest {

    @NotNull(message = "Amount is required")
    private BigDecimal amount;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private String description;

    // Getters & Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
