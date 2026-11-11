package com.nurulaqilahahmad.expense_reimbursement.controller;

import com.nurulaqilahahmad.expense_reimbursement.service.AppUserService;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController     // combines @Controller and @ResponseBody that returns JSON responses
@RequestMapping("/api/users")
public class AppUserController {

    private final AppUserService appUserService;

    public AppUserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }
}
