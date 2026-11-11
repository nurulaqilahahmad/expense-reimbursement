package com.nurulaqilahahmad.expense_reimbursement.controller;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @GetMapping("/me")
    public Map<String, Object> getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return Map.of(
                "subject", Objects.requireNonNull(jwt.getSubject()),
                "username", Objects.requireNonNull(jwt.getClaimAsString("preferred_username")),
                "email", Objects.requireNonNull(jwt.getClaimAsString("email"))
        );
    }

    @GetMapping("/authorities")
    public Map<String, Object> getAuthorities(Authentication authentication, @AuthenticationPrincipal Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaimAsMap("resource_access");
        return Map.of(
                "username", authentication.getName(),
                "authorities", authentication.getAuthorities(),
                "resourceAccess", resourceAccess.containsKey("expense-reimbursement-api")  // This will give you the roles assigned to the user in Keycloak
        );
    }
}
