package com.nurulaqilahahmad.expense_reimbursement.entity;

import org.springframework.lang.Nullable;

public enum EnumExpenseCategory {
    TRANSPORT("TRANSPORT"),
    MEAL("MEAL"),
    ACCOMMODATION("ACCOMMODATION"),
    PARKING("PARKING"),
    OFFICE_SUPPLIES("OFFICE_SUPPLIES"),
    OTHER("OTHER");

    private final String id;

    EnumExpenseCategory(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EnumExpenseCategory fromId(String id) {
        for (EnumExpenseCategory at : EnumExpenseCategory.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}
