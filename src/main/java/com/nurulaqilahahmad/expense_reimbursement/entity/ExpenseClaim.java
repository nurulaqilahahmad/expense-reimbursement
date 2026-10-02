package com.nurulaqilahahmad.expense_reimbursement.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "expense_claim")
@Getter
@Setter
public class ExpenseClaim {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private String title;

    @Column(length = 1000)
    private String description;

    private LocalDate submissionDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EnumExpenseClaimStatus status = EnumExpenseClaimStatus.DRAFT;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal totalAmount = BigDecimal.ZERO;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(
            mappedBy = "claim",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private List<ExpenseItem> items = new ArrayList<>();

    @PrePersist
    public void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    public void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}
