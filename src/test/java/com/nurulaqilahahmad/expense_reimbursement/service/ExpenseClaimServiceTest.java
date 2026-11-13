package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.dto.request.RejectExpenseClaimRequest;
import com.nurulaqilahahmad.expense_reimbursement.dto.response.ExpenseClaimResponse;
import com.nurulaqilahahmad.expense_reimbursement.entity.AppUser;
import com.nurulaqilahahmad.expense_reimbursement.entity.EnumExpenseClaimStatus;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseClaim;
import com.nurulaqilahahmad.expense_reimbursement.entity.ExpenseItem;
import com.nurulaqilahahmad.expense_reimbursement.exception.ExpenseClaimNotFoundException;
import com.nurulaqilahahmad.expense_reimbursement.exception.InvalidExpenseClaimStateException;
import com.nurulaqilahahmad.expense_reimbursement.repository.ExpenseClaimRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ExpenseClaimServiceTest {

    @Mock
    private ExpenseClaimRepository expenseClaimRepository;

    @Mock
    private AppUserService appUserService;

    private ExpenseClaimService expenseClaimService;

    @BeforeEach
    void setUp() {
        expenseClaimService = new ExpenseClaimService(
                expenseClaimRepository,
                appUserService
        );
    }

    @Test
    void approveClaim_whenSubmitted_shouldApproveClaim() {
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            claim = SUBMITTED
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);

        /*
            When:
            approveClaim()
        */
        when(expenseClaimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(expenseClaimRepository.save(claim)).thenReturn(claim);
        expenseClaimService.approveClaim(claimId);

        /*
            Then:
            status = APPROVED
            approvedAt != null
            repository.save() called
        */
        assertEquals(EnumExpenseClaimStatus.APPROVED, claim.getStatus());
        assertNotNull(claim.getApprovedAt());
        verify(expenseClaimRepository).save(claim);
    }

    @Test
    void approveClaim_whenDraft_shouldThrowException() {
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            claim = DRAFT
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.DRAFT);

        /*
            When:
            approveClaim()

            Then:
            throws InvalidExpenseClaimStateException
            no repository.save()
        */
        when(expenseClaimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        InvalidExpenseClaimStateException exception =
                assertThrows(
                        InvalidExpenseClaimStateException.class,
                        () -> expenseClaimService.approveClaim(claimId)
                );
        assertEquals("Only SUBMITTED claims can be approved", exception.getMessage());
        verify(expenseClaimRepository, never()).save(any());
    }

    @Test
    void rejectClaim_whenSubmitted_shouldRejectClaim() {
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            claim = SUBMITTED
            rejection_reason = "Receipt is missing"
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);
        RejectExpenseClaimRequest request = new RejectExpenseClaimRequest();
        request.setReason("Receipt is missing");

        /*
            When:
            rejectClaim()
        */
        when(expenseClaimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(expenseClaimRepository.save(claim)).thenReturn(claim);
        expenseClaimService.rejectClaim(claimId,request);

        /*
            Then:
            status = REJECTED
            rejection_reason = "Receipt is missing"
            rejectedAt != null
        */
        assertEquals(EnumExpenseClaimStatus.REJECTED, claim.getStatus());
        assertEquals("Receipt is missing", claim.getRejectionReason());
        assertNotNull(claim.getRejectedAt());
    }

    @Test
    void reviseClaim_whenRejectedAndOwnedByEmployee_shouldReturnToDraft() {
        UUID claimId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        String identityProviderId = "keycloak-user-123";
        AppUser employee = new AppUser();
        employee.setId(employeeId);

        /*
            Given:
            claim = REJECTED
            rejection_reason = "Missing receipt"
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setEmployee(employee);
        claim.setStatus(EnumExpenseClaimStatus.REJECTED);
        claim.setRejectionReason("Missing receipt");
        claim.setRejectedAt(LocalDateTime.now());

        /*
            When:
            reviseClaim()
        */
        when(appUserService.getByIdentityProviderId(identityProviderId)).thenReturn(employee);
        when(expenseClaimRepository.findByIdAndEmployee_Id(claimId, employeeId)).thenReturn(Optional.of(claim));
        when(expenseClaimRepository.save(claim)).thenReturn(claim);
        expenseClaimService.reviseClaim(claimId, identityProviderId);

        /*
            Then:
            status = REJECTED
            rejection_reason = "Missing receipt"
            rejectedAt != null
        */
        assertEquals(EnumExpenseClaimStatus.DRAFT, claim.getStatus());
        assertNull(claim.getRejectionReason());
        assertNull(claim.getRejectedAt());
        verify(appUserService).getByIdentityProviderId(identityProviderId);
        verify(expenseClaimRepository).findByIdAndEmployee_Id(claimId, employeeId);
        verify(expenseClaimRepository).save(claim);
    }

    @Test
    void reviseClaim_whenClaimNotOwnedByEmployee_shouldThrowNotFound() {
        UUID claimId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        /*
            Given:
            appUser = employee1-sub
        */
        String identityProviderId = "employee1-sub";
        AppUser employee = new AppUser();
        employee.setId(employeeId);

        /*
            When:
            reviseClaim()
        */
        when(appUserService.getByIdentityProviderId(identityProviderId)).thenReturn(employee);
        when(expenseClaimRepository.findByIdAndEmployee_Id(claimId, employeeId)).thenReturn(Optional.empty());

        /*
            Then:
            throws ExpenseClaimNotFoundException
            no repository.save()
        */
        assertThrows(
                ExpenseClaimNotFoundException.class,
                () -> expenseClaimService.reviseClaim(claimId, identityProviderId)
        );
        verify(expenseClaimRepository, never()).save(any());
    }

    @Test
    void reviseClaim_whenNotRejectedAndOwnedByEmployee_shouldThrowException() {
        UUID claimId = UUID.randomUUID();
        UUID employeeId = UUID.randomUUID();

        String identityProviderId = "employee1-sub";
        AppUser employee = new AppUser();
        employee.setId(employeeId);

        /*
            Given:
            claim = SUBMITTED
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);

        /*
            When:
            reviseClaim()

            Then:
            throws InvalidExpenseClaimStateException
            no repository.save()
        */
        when(appUserService.getByIdentityProviderId(identityProviderId)).thenReturn(employee);
        when(expenseClaimRepository.findByIdAndEmployee_Id(claimId, employeeId)).thenReturn(Optional.of(claim));

        assertThrows(
                InvalidExpenseClaimStateException.class,
                () -> expenseClaimService.reviseClaim(claimId, identityProviderId)
        );
        verify(expenseClaimRepository, never()).save(any());
    }

    @Test
    void markClaimAsPaid_whenApproved_shouldMarkAsPaid() {
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            claim = APPROVED
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.APPROVED);

        /*
            When:
            markClaimAsPaid()
        */
        when(expenseClaimRepository.findById(claimId)).thenReturn(Optional.of(claim));
        when(expenseClaimRepository.save(claim)).thenReturn(claim);
        expenseClaimService.markClaimAsPaid(claimId);

        /*
            Then:
            claim = PAID
            paidAt != null
        */
        assertEquals(EnumExpenseClaimStatus.PAID,claim.getStatus());
        assertNotNull(claim.getPaidAt());
        verify(expenseClaimRepository).save(claim);
    }

    @Test
    void markClaimAsPaid_whenSubmitted_shouldThrowException() {
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            claim = SUBMITTED
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setStatus(EnumExpenseClaimStatus.SUBMITTED);

        /*
            When:
            markClaimAsPaid()

            Then:
            throws InvalidExpenseClaimStateException
            no repository.save()
        */
        when(expenseClaimRepository.findById(claimId)).thenReturn(Optional.of(claim));

        assertThrows(
                InvalidExpenseClaimStateException.class,
                () -> expenseClaimService.markClaimAsPaid(claimId)
        );
        verify(expenseClaimRepository, never()).save(any());
    }

    @Test
    void submitClaim_whenOwnedByEmployee_shouldSubmit() {
        UUID userId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();

        String identityProviderId = "keycloak-user-123";
        AppUser employee = new AppUser();
        employee.setId(userId);

        /*
            Given:
            claim = DRAFT
        */
        ExpenseClaim claim = new ExpenseClaim();
        claim.setId(claimId);
        claim.setEmployee(employee);
        claim.setStatus(EnumExpenseClaimStatus.DRAFT);
        ExpenseItem item = new ExpenseItem();
        item.setAmount(new BigDecimal("20.00"));
        claim.setItems(List.of(item));  // Ensure the claim has at least one item

        /*
            When:
            submitClaim()
        */
        when(appUserService.getByIdentityProviderId(identityProviderId)).thenReturn(employee);
        when(expenseClaimRepository.findByIdAndEmployee_Id(claimId,userId)).thenReturn(Optional.of(claim));
        when(expenseClaimRepository.save(claim)).thenReturn(claim);
        expenseClaimService.submitClaim(claimId, identityProviderId);

        /*
            Then:
            status = SUBMITTED
            submissionDate != null
        */
        assertEquals(EnumExpenseClaimStatus.SUBMITTED, claim.getStatus());
        assertNotNull(claim.getSubmissionDate());
    }

    @Test
    void submitClaim_whenClaimNotOwnedByEmployee_shouldThrowNotFound() {
        UUID userId = UUID.randomUUID();
        UUID claimId = UUID.randomUUID();

        /*
            Given:
            appUser = employee1-sub
        */
        String identityProviderId = "employee1-sub";
        AppUser employee = new AppUser();
        employee.setId(userId);

        /*
            When:
            submitClaim()

            Then:
            throws ExpenseClaimNotFoundException
            no repository.save()
        */
        when(appUserService.getByIdentityProviderId(identityProviderId)).thenReturn(employee);
        when(expenseClaimRepository.findByIdAndEmployee_Id(claimId, userId)).thenReturn(Optional.empty());

        assertThrows(
                ExpenseClaimNotFoundException.class,
                () -> expenseClaimService.submitClaim(claimId, identityProviderId)
        );
        verify(expenseClaimRepository, never()).save(any());
    }
}