package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.RejectExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.UpdateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.entity.AppUser;
import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseClaimStatus;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;
import com.nurulaqilahahmad.expense_reimbursement.exception.ExpenseClaimNotFoundException;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.exception.InvalidExpenseClaimStateException;
import com.nurulaqilahahmad.expense_reimbursement.mapper.ExpenseClaimMapper;
import com.nurulaqilahahmad.expense_reimbursement.repository.ExpenseClaimRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseClaimService {

    private final ExpenseClaimRepository expenseClaimRepository;
    private final AppUserService appUserService;

    public ExpenseClaimService(ExpenseClaimRepository expenseClaimRepository, AppUserService appUserService) {
        this.expenseClaimRepository = expenseClaimRepository;
        this.appUserService = appUserService;
    }

    // ===============================================================================================================
    // EMPLOYEE services
    // ===============================================================================================================

    public ExpenseClaimResponse createClaim(CreateExpenseClaimRequest request, String identityProviderId) {
        AppUser employee = appUserService.getByIdentityProviderId(identityProviderId);

        ExpenseClaim claim = ExpenseClaimMapper.toEntity(request);

        BigDecimal totalAmount = claim.getItems()
                .stream()
                .map(ExpenseItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        claim.setEmployee(employee);
        claim.setTotalAmount(totalAmount);

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse updateClaim(UUID id, UpdateExpenseClaimRequest request, String identityProviderId) {
        ExpenseClaim existingClaim = findClaimByEmployee_Id(id, identityProviderId);

        if (existingClaim.getStatus() != EnumExpenseClaimStatus.DRAFT) {
            throw new InvalidExpenseClaimStateException("Only DRAFT claims can be edited");
        }

        existingClaim.setTitle(request.getTitle());
        existingClaim.setDescription(request.getDescription());

        ExpenseClaim savedExistingClaim = expenseClaimRepository.save(existingClaim);

        return ExpenseClaimMapper.toResponse(savedExistingClaim);
    }

    public ExpenseClaimResponse submitClaim(UUID id, String identityProviderId) {

        ExpenseClaim claim = findClaimByEmployee_Id(id, identityProviderId);

        if (claim.getStatus() != EnumExpenseClaimStatus.DRAFT) {
            throw new InvalidExpenseClaimStateException("Only DRAFT claims can be submitted");
        }

        if (claim.getItems() == null || claim.getItems().isEmpty()) {
            throw new InvalidExpenseClaimStateException("A claim must contain at least one expense item before submission");
        }

        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);
        claim.setSubmissionDate(LocalDateTime.now());

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse reviseClaim(UUID id, String identityProviderId) {

        ExpenseClaim claim = findClaimByEmployee_Id(id, identityProviderId);

        if (claim.getStatus() != EnumExpenseClaimStatus.REJECTED) {
            throw new InvalidExpenseClaimStateException("Only REJECTED claims can be revised");
        }

        claim.setStatus(EnumExpenseClaimStatus.DRAFT);
        claim.setRejectionReason(null);
        claim.setRejectedAt(null);

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public void deleteClaim(UUID id) {
        ExpenseClaim claim = findClaimById(id);
        // not expenseClaimRepository.deleteById(id); bcs to control what happens if the claim doesn't exist or draft
        if (claim.getStatus() != EnumExpenseClaimStatus.DRAFT) {
            throw new InvalidExpenseClaimStateException("Only DRAFT claims can be deleted");
        }

        expenseClaimRepository.delete(claim);
    }

    // ===============================================================================================================
    // MANAGER services
    // ===============================================================================================================

    public ExpenseClaimResponse approveClaim(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.SUBMITTED) {
            throw new InvalidExpenseClaimStateException("Only SUBMITTED claims can be approved");
        }

        claim.setStatus(EnumExpenseClaimStatus.APPROVED);
        claim.setApprovedAt(LocalDateTime.now());

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse rejectClaim(UUID id, RejectExpenseClaimRequest request) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.SUBMITTED) {
            throw new InvalidExpenseClaimStateException("Only SUBMITTED claims can be rejected");
        }

        claim.setStatus(EnumExpenseClaimStatus.REJECTED);
        claim.setRejectionReason(request.getReason());
        claim.setRejectedAt(LocalDateTime.now());

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    // ===============================================================================================================
    // FINANCE services
    // ===============================================================================================================

    public ExpenseClaimResponse markClaimAsPaid(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.APPROVED) {
            throw new InvalidExpenseClaimStateException("Only APPROVED claims can be marked as paid");
        }

        claim.setStatus(EnumExpenseClaimStatus.PAID);
        claim.setPaidAt(LocalDateTime.now());

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    // ===============================================================================================================
    // HELPER METHODS
    // ===============================================================================================================

    public List<ExpenseClaimResponse> getAllClaims() {
        return expenseClaimRepository.findAll()
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();
    }

    public List<ExpenseClaimResponse> getClaimsByEmployee_Id(String identityProviderId) {
        AppUser employee = appUserService.getByIdentityProviderId(identityProviderId);

        return expenseClaimRepository
                .findByEmployee_Id(employee.getId())
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();
    }

    public ExpenseClaim findClaimById(UUID id) {
        return expenseClaimRepository.findById(id)
                .orElseThrow(() -> new ExpenseClaimNotFoundException(id));
    }

    private ExpenseClaim findClaimByEmployee_Id(UUID id, String identityProviderId) {
        AppUser employee = appUserService.getByIdentityProviderId(identityProviderId);

        return expenseClaimRepository.findByIdAndEmployee_Id(id, employee.getId())
                .orElseThrow(() ->
                        new ExpenseClaimNotFoundException(id)
                );
    }

    public ExpenseClaimResponse getClaimById(UUID id) {
        ExpenseClaim claim = findClaimById(id);
        return ExpenseClaimMapper.toResponse(claim);
    }

    public ExpenseClaimResponse getClaimByEmployee_Id(UUID id, String identityProviderId) {
        AppUser employee = appUserService.getByIdentityProviderId(identityProviderId);

        ExpenseClaim claim = expenseClaimRepository.findByIdAndEmployee_Id(id, employee.getId())
                        .orElseThrow(() -> new ExpenseClaimNotFoundException(id));

        return ExpenseClaimMapper.toResponse(claim);
    }

    public List<ExpenseClaimResponse> getPendingClaims() {
        return expenseClaimRepository.findByStatus(EnumExpenseClaimStatus.SUBMITTED)
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();
    }

    public List<ExpenseClaimResponse> getApprovedClaims() {
        return expenseClaimRepository.findByStatus(EnumExpenseClaimStatus.APPROVED)
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();
    }
}
