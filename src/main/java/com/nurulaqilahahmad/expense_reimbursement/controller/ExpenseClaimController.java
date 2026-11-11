package com.nurulaqilahahmad.expense_reimbursement.controller;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.RejectExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.UpdateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.service.ExpenseClaimService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
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

    @GetMapping
    public ResponseEntity<List<ExpenseClaimResponse>> getAllClaims() {
        return ResponseEntity.ok(expenseClaimService.getAllClaims());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> getClaimById(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.getClaimById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ExpenseClaimResponse> updateClaim(@PathVariable UUID id, @Valid @RequestBody UpdateExpenseClaimRequest request, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(expenseClaimService.updateClaim(id, request, jwt.getSubject()));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteClaim(@PathVariable UUID id) {
        expenseClaimService.deleteClaim(id);
        return ResponseEntity.noContent().build();
    }

    // ===============================================================================================================
    // EMPLOYEE endpoints
    // ===============================================================================================================

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping
    public ResponseEntity<ExpenseClaimResponse> createClaim(@Valid @RequestBody CreateExpenseClaimRequest request, @AuthenticationPrincipal Jwt jwt) {
        ExpenseClaimResponse response = expenseClaimService.createClaim(request, jwt.getSubject());
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/claims-by-employee")
    public ResponseEntity<List<ExpenseClaimResponse>> getClaimsByEmployee_Id(@AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(expenseClaimService.getClaimsByEmployee_Id(jwt.getSubject()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @GetMapping("/claim-by-employee/{id}")
    public ResponseEntity<ExpenseClaimResponse> getClaimByEmployee_Id(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(expenseClaimService.getClaimByEmployee_Id(id, jwt.getSubject()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/submit")
    public ResponseEntity<ExpenseClaimResponse> submitClaim(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(expenseClaimService.submitClaim(id, jwt.getSubject()));
    }

    @PreAuthorize("hasRole('EMPLOYEE')")
    @PostMapping("/{id}/revise")
    public ResponseEntity<ExpenseClaimResponse> reviseClaim(@PathVariable UUID id, @AuthenticationPrincipal Jwt jwt) {
        return ResponseEntity.ok(expenseClaimService.reviseClaim(id, jwt.getSubject()));
    }

    // ===============================================================================================================
    // MANAGER endpoints
    // ===============================================================================================================

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/approve")
    public ResponseEntity<ExpenseClaimResponse> approveClaim(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.approveClaim(id));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @PostMapping("/{id}/reject")
    public ResponseEntity<ExpenseClaimResponse> rejectClaim(@PathVariable UUID id, @Valid @RequestBody RejectExpenseClaimRequest request) {
        return ResponseEntity.ok(expenseClaimService.rejectClaim(id, request));
    }

    @PreAuthorize("hasRole('MANAGER')")
    @GetMapping("/pending")
    public ResponseEntity<List<ExpenseClaimResponse>> getPendingClaims() {
        return ResponseEntity.ok(expenseClaimService.getPendingClaims());
    }

    // ===============================================================================================================
    // FINANCE endpoints
    // ===============================================================================================================

    @PreAuthorize("hasRole('FINANCE')")
    @PostMapping("/{id}/pay")
    public ResponseEntity<ExpenseClaimResponse> markClaimAsPaid(@PathVariable UUID id) {
        return ResponseEntity.ok(expenseClaimService.markClaimAsPaid(id));
    }

    @PreAuthorize("hasRole('FINANCE')")
    @GetMapping("/approved")
    public ResponseEntity<List<ExpenseClaimResponse>> getApprovedClaims() {
        return ResponseEntity.ok(expenseClaimService.getApprovedClaims());
    }
}