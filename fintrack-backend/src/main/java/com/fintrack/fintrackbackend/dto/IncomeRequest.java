package com.fintrack.fintrackbackend.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;

public class IncomeRequest {
    @NotNull(message = "Amount is required")
    @Min(value = 0, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Category ID is required")
    private Integer categoryId;

    private String description;

    // Getters and Setters
    public BigDecimal getAmount() { return amount; }
    public void setAmount(BigDecimal amount) { this.amount = amount; }

    public Integer getCategoryId() { return categoryId; }
    public void setCategoryId(Integer categoryId) { this.categoryId = categoryId; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
}
