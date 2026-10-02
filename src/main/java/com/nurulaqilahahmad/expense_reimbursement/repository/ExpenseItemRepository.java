package com.nurulaqilahahmad.expense_reimbursement.repository;

import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ExpenseItemRepository extends JpaRepository<ExpenseItem, UUID> {
}
