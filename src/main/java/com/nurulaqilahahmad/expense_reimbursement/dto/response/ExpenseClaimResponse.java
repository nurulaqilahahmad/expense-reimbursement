package com.nurulaqilahahmad.expense_reimbursement.dto.response;

import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseClaimStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class ExpenseClaimResponse {

    private UUID id;
    private String title;
    private String description;
    private LocalDate submissionDate;
    private EnumExpenseClaimStatus status;
    private BigDecimal totalAmount;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<ExpenseItemResponse> items;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDate submissionDate) {
        this.submissionDate = submissionDate;
    }

    public EnumExpenseClaimStatus getStatus() {
        return status;
    }

    public void setStatus(EnumExpenseClaimStatus status) {
        this.status = status;
    }

    public BigDecimal getTotalAmount() {
        return totalAmount;
    }

    public void setTotalAmount(BigDecimal totalAmount) {
        this.totalAmount = totalAmount;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public List<ExpenseItemResponse> getItems() {
        return items;
    }

    public void setItems(List<ExpenseItemResponse> items) {
        this.items = items;
    }
}