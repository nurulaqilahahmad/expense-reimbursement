package com.nurulaqilahahmad.expense_reimbursement.dto.response;

import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseCategory;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class ExpenseItemResponse {

    private UUID id;
    private String description;
    private BigDecimal amount;
    private LocalDate expenseDate;
    private EnumExpenseCategory category;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

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