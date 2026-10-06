package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.UpdateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseClaimStatus;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;
import com.nurulaqilahahmad.expense_reimbursement.exception.ExpenseClaimNotFoundException;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.exception.InvalidExpenseClaimStateException;
import com.nurulaqilahahmad.expense_reimbursement.mapper.ExpenseClaimMapper;
import com.nurulaqilahahmad.expense_reimbursement.repository.ExpenseClaimRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
public class ExpenseClaimService {

    private final ExpenseClaimRepository expenseClaimRepository;

    public ExpenseClaimService(ExpenseClaimRepository expenseClaimRepository) {
        this.expenseClaimRepository = expenseClaimRepository;
    }

    public ExpenseClaimResponse createClaim(CreateExpenseClaimRequest request) {

        ExpenseClaim claim = ExpenseClaimMapper.toEntity(request);

        BigDecimal totalAmount = claim.getItems()
                .stream()
                .map(ExpenseItem::getAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        claim.setTotalAmount(totalAmount);

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public List<ExpenseClaimResponse> getAllClaims() {
        return expenseClaimRepository.findAll()
                .stream()
                .map(ExpenseClaimMapper::toResponse)
                .toList();
    }

    public ExpenseClaim findClaimById(UUID id) {
        return expenseClaimRepository.findById(id)
                .orElseThrow(() -> new ExpenseClaimNotFoundException(id));
    }

    public ExpenseClaimResponse getClaimById(UUID id) {
        ExpenseClaim claim = findClaimById(id);
        return ExpenseClaimMapper.toResponse(claim);
    }

    public ExpenseClaimResponse updateClaim(UUID id, UpdateExpenseClaimRequest request) {
        ExpenseClaim existingClaim = findClaimById(id);

        if (existingClaim.getStatus() != EnumExpenseClaimStatus.DRAFT) {
            throw new InvalidExpenseClaimStateException("Only DRAFT claims can be edited");
        }

        existingClaim.setTitle(request.getTitle());
        existingClaim.setDescription(request.getDescription());

        ExpenseClaim savedExistingClaim = expenseClaimRepository.save(existingClaim);

        return ExpenseClaimMapper.toResponse(savedExistingClaim);
    }

    public ExpenseClaimResponse submitClaim(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.DRAFT) {
            throw new InvalidExpenseClaimStateException("Only DRAFT claims can be submitted");
        }

        if (claim.getItems() == null || claim.getItems().isEmpty()) {
            throw new InvalidExpenseClaimStateException("A claim must contain at least one expense item before submission");
        }

        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);
        claim.setSubmissionDate(LocalDate.now());

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse approveClaim(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.SUBMITTED) {
            throw new InvalidExpenseClaimStateException("Only SUBMITTED claims can be approved");
        }

        claim.setStatus(EnumExpenseClaimStatus.APPROVED);

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse rejectClaim(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.SUBMITTED) {
            throw new InvalidExpenseClaimStateException("Only SUBMITTED claims can be rejected");
        }

        claim.setStatus(EnumExpenseClaimStatus.REJECTED);

        ExpenseClaim savedClaim = expenseClaimRepository.save(claim);

        return ExpenseClaimMapper.toResponse(savedClaim);
    }

    public ExpenseClaimResponse markClaimAsPaid(UUID id) {

        ExpenseClaim claim = findClaimById(id);

        if (claim.getStatus() != EnumExpenseClaimStatus.APPROVED) {
            throw new InvalidExpenseClaimStateException("Only APPROVED claims can be marked as paid");
        }

        claim.setStatus(EnumExpenseClaimStatus.PAID);

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
}
