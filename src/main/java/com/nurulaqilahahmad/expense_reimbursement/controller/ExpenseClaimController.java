package com.nurulaqilahahmad.expense_reimbursement.controller;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.RejectExpenseClaimRequest;
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
        ExpenseClaimResponse response = expenseClaimService.createClaim(request);
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

    @PostMapping("/{id}/submit")
    public ResponseEntity<ExpenseClaimResponse> submitClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.submitClaim(id));
    }

    @PostMapping("/{id}/approve")
    public ResponseEntity<ExpenseClaimResponse> approveClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.approveClaim(id));
    }

    @PostMapping("/{id}/reject")
    public ResponseEntity<ExpenseClaimResponse> rejectClaim(@PathVariable UUID id, @Valid @RequestBody RejectExpenseClaimRequest request) {
        return ResponseEntity.ok(expenseClaimService.rejectClaim(id, request));
    }

    @PostMapping("/{id}/revise")
    public ResponseEntity<ExpenseClaimResponse> reviseClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.reviseClaim(id));
    }

    @PostMapping("/{id}/pay")
    public ResponseEntity<ExpenseClaimResponse> markClaimAsPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.markClaimAsPaid(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable UUID id) {
        expenseClaimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }
}