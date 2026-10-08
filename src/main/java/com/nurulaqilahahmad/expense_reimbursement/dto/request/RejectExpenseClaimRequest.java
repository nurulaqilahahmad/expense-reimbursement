package com.nurulaqilahahmad.expense_reimbursement.dto.request;

import jakarta.validation.constraints.NotBlank;

public class RejectExpenseClaimRequest {

    @NotBlank
    private String reason;

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }
}