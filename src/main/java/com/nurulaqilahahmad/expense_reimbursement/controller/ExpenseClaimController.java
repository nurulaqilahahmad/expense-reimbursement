package com.nurulaqilahahmad.expense_reimbursement.controller;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.UpdateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.service.ExpenseClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController     // combines @Controller and @ResponseBody that returns JSON responses
@RequestMapping("/api/claims")
public class ExpenseClaimController {

    private final ExpenseClaimService expenseClaimService;

    public ExpenseClaimController(ExpenseClaimService expenseClaimService) {
        this.expenseClaimService = expenseClaimService;
    }

    @PostMapping
    public ResponseEntity<ExpenseClaimResponse> createClaim(@Valid @RequestBody CreateExpenseClaimRequest request) {
        ExpenseClaimResponse response =expenseClaimService.createClaim(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public ResponseEntity<List<ExpenseClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(expenseClaimService.getAllClaims());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> getClaimById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.getClaimById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> updateClaim(@PathVariable UUID id, @Valid @RequestBody UpdateExpenseClaimRequest request) {
        return ResponseEntity.ok(expenseClaimService.updateClaim(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable UUID id) {
        expenseClaimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}