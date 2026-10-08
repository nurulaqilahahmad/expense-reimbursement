package com.nurulaqilahahmad.expense_reimbursement.entity;

import org.springframework.lang.Nullable;

public enum EnumUserRole {

    EMPLOYEE("EMPLOYEE"),
    MANAGER("MANAGER"),
    FINANCE("FINANCE"),
    ADMIN("ADMIN");

    private final String id;

    EnumUserRole(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    @Nullable
    public static EnumUserRole fromId(String id) {
        for (EnumUserRole at : EnumUserRole.values()) {
            if (at.getId().equals(id)) {
                return at;
            }
        }
        return null;
    }
}