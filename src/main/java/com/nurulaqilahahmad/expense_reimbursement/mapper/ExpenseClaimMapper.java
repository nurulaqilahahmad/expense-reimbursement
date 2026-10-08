package com.nurulaqilahahmad.expense_reimbursement.mapper;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.ExpenseItemRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseItemResponse;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;

import java.util.List;

public class ExpenseClaimMapper {

    private ExpenseClaimMapper() {}

    public static ExpenseClaim toEntity(CreateExpenseClaimRequest request) {

        ExpenseClaim claim = new ExpenseClaim();

        claim.setTitle(request.getTitle());
        claim.setDescription(request.getDescription());

        List<ExpenseItem> items = request.getItems()
                .stream()
                .map(itemRequest -> toEntity(itemRequest, claim))
                .toList();

        claim.setItems(items);

        return claim;
    }

    private static ExpenseItem toEntity(ExpenseItemRequest request, ExpenseClaim claim) {

        ExpenseItem item = new ExpenseItem();

        item.setDescription(request.getDescription());
        item.setAmount(request.getAmount());
        item.setExpenseDate(request.getExpenseDate());
        item.setCategory(request.getCategory());

        item.setClaim(claim);

        return item;
    }

    public static ExpenseClaimResponse toResponse(ExpenseClaim claim) {

        ExpenseClaimResponse response = new ExpenseClaimResponse();

        response.setId(claim.getId());
        response.setTitle(claim.getTitle());
        response.setDescription(claim.getDescription());
        response.setSubmissionDate(claim.getSubmissionDate());
        response.setStatus(claim.getStatus());
        response.setTotalAmount(claim.getTotalAmount());
        response.setCreatedAt(claim.getCreatedAt());
        response.setUpdatedAt(claim.getUpdatedAt());
        response.setApprovedAt(claim.getApprovedAt());
        response.setRejectedAt(claim.getRejectedAt());
        response.setRejectionReason(claim.getRejectionReason());
        response.setPaidAt(claim.getPaidAt());

        List<ExpenseItemResponse> items = claim.getItems()
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();

        response.setItems(items);

        return response;
    }

    private static ExpenseItemResponse toResponse(ExpenseItem item) {

        ExpenseItemResponse response = new ExpenseItemResponse();

        response.setId(item.getId());
        response.setDescription(item.getDescription());
        response.setAmount(item.getAmount());
        response.setExpenseDate(item.getExpenseDate());
        response.setCategory(item.getCategory());

        return response;
    }
}