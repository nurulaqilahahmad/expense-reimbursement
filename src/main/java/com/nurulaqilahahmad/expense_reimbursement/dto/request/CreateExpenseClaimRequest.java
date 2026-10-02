package com.nurulaqilahahmad.expense_reimbursement.dto.request;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.List;

public class CreateExpenseClaimRequest {

    @NotBlank
    private String title;

    private String description;

    @NotEmpty
    @Valid
    private List<ExpenseItemRequest> items;

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

    public List<ExpenseItemRequest> getItems() {
        return items;
    }

    public void setItems(List<ExpenseItemRequest> items) {
        this.items = items;
    }
}