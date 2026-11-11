package com.nurulaqilahahmad.expense_reimbursement.repository;

import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseClaimStatus;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ExpenseClaimRepository extends JpaRepository<ExpenseClaim, UUID> {

    List<ExpenseClaim> findByEmployee_Id(UUID employeeId);

    Optional<ExpenseClaim> findByIdAndEmployee_Id(UUID id, UUID employeeId);

    List<ExpenseClaim> findByStatus(EnumExpenseClaimStatus status);
}
