package com.nurulaqilahahmad.expense_reimbursement.service;

import com.nurulaqilahahmad.expense_reimbursement.entity.AppUser;
import com.nurulaqilahahmad.expense_reimbursement.repository.AppUserRepository;
import org.springframework.stereotype.Service;

@Service
public class AppUserService {

    private final AppUserRepository appUserRepository;

    public AppUserService(AppUserRepository appUserRepository) {
        this.appUserRepository = appUserRepository;
    }

    public AppUser getByIdentityProviderId(String identityProviderId) {
        return appUserRepository
                .findByIdentityProviderId(identityProviderId)
                .orElseThrow(() -> new RuntimeException("App user not found for identity provider ID: " + identityProviderId));
    }
}