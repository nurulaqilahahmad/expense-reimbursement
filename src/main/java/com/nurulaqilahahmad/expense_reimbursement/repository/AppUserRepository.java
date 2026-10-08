package com.nurulaqilahahmad.expense_reimbursement.repository;

import com.nurulaqilahahmad.expense_reimbursement.entity.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface AppUserRepository extends JpaRepository<AppUser, UUID> {

    Optional<AppUser> findByIdentityProviderId(String identityProviderId);

    Optional<AppUser> findByUsername(String username);
}