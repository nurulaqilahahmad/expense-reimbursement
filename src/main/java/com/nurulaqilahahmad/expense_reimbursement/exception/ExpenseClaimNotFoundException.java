package com.nurulaqilahahmad.expense_reimbursement.exception;

import java.util.UUID;

public class ExpenseClaimNotFoundException extends RuntimeException {
    public ExpenseClaimNotFoundException(UUID id) {
        super("Expense claim not found: " + id);
    }
}
