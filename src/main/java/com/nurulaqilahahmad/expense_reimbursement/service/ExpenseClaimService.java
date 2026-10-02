package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.exception.ExpenseClaimNotFoundException;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.repository.ExpenseClaimRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ExpenseClaimService {

    private final ExpenseClaimRepository expenseClaimRepository;

    public ExpenseClaimService(ExpenseClaimRepository expenseClaimRepository) {
        this.expenseClaimRepository = expenseClaimRepository;
    }

    public ExpenseClaim createClaim(ExpenseClaim claim) {
        return expenseClaimRepository.save(claim);
    }

    public List<ExpenseClaim> getAllClaims() {
        return expenseClaimRepository.findAll();
    }

    public ExpenseClaim getClaimById(UUID id) {
        return expenseClaimRepository.findById(id)
                .orElseThrow(() -> new ExpenseClaimNotFoundException(id));
    }

    public ExpenseClaim updateClaim(UUID id, ExpenseClaim updatedClaim) {
        ExpenseClaim existingClaim = getClaimById(id);

        existingClaim.setTitle(updatedClaim.getTitle());
        existingClaim.setDescription(updatedClaim.getDescription());

        return expenseClaimRepository.save(existingClaim);
    }

    public void deleteClaim(UUID id) {
        ExpenseClaim claim = getClaimById(id); // not expenseClaimRepository.deleteById(id); bcs to control what happens if the claim doesn't exist
        expenseClaimRepository.delete(claim);
    }
}
