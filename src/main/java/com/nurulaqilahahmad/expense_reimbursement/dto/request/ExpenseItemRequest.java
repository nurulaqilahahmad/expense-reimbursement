package com.nurulaqilahahmad.expense_reimbursement.dto.request;

import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseCategory;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class ExpenseItemRequest {

    @NotBlank
    private String description;

    @NotNull
    @DecimalMin(value = "0.01")
    private BigDecimal amount;

    @NotNull
    private LocalDate expenseDate;

    @NotNull
    private EnumExpenseCategory category;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public LocalDate getExpenseDate() {
        return expenseDate;
    }

    public void setExpenseDate(LocalDate expenseDate) {
        this.expenseDate = expenseDate;
    }

    public EnumExpenseCategory getCategory() {
        return category;
    }

    public void setCategory(EnumExpenseCategory category) {
        this.category = category;
    }
}