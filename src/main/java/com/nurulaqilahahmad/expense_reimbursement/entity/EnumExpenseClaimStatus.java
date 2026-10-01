package com.nurulaqilahahmad.expense_reimbursement.entity;

import org.springframework.lang.Nullable;

public enum EnumExpenseClaimStatus {
    DRAFT("DRAFT"),
    SUBMITTED("SUBMITTED"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    PAID("PAID");

    private final String id;

    EnumExpenseClaimStatus(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EnumExpenseClaimStatus fromId(String id) {
        for (EnumExpenseClaimStatus at : EnumExpenseClaimStatus.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
