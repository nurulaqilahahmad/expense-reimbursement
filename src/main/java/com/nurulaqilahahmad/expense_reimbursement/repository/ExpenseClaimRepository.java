package com.nurulaqilahahmad.expense_reimbursement.repository;

import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, UUID> {
}
