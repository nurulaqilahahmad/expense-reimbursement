package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.CreateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.request.UpdateExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;
import com.nurulaqilahahmad.expense_reimbursement.exception.ExpenseClaimNotFoundException;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.mapper.ExpenseClaimMapper;
import com.nurulaqilahahmad.expense_reimbursement.repository.ExpenseClaimRepository;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
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

        existingClaim.setTitle(request.getTitle());
        existingClaim.setDescription(request.getDescription());

        ExpenseClaim savedExistingClaim = expenseClaimRepository.save(existingClaim);

        return ExpenseClaimMapper.toResponse(savedExistingClaim);
    }

    public void deleteClaim(UUID id) {
        ExpenseClaim claim = findClaimById(id); // not expenseClaimRepository.deleteById(id); bcs to control what happens if the claim doesn't exist
        expenseClaimRepository.delete(claim);
    }
}
