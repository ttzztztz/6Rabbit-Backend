package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Credits.DepositAdminForm;
import com.rabbit.backend.Bean.Credits.DepositSubmitForm;
import com.rabbit.backend.Service.DepositService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/deposit")
public class DepositController {
    private DepositService depositService;

    @Autowired
    public DepositController(DepositService depositService) {
        this.depositService = depositService;
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> pay(@RequestBody @Valid DepositSubmitForm form, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();

        if (depositService.submitIsFrequent(uid)) {
            return GeneralResponse.generate(400, "Pay too frequent, try again after 10 minutes.");
        }
        String cid = depositService.submitDeposit(form.getCredits(), uid);
        depositService.submitFrequentSet(uid);
        return GeneralResponse.generate(200, cid);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> adminVerify(@RequestBody @Valid DepositAdminForm form) {
        depositService.setDeposit(form.getCid(), form.getStatus());
        return GeneralResponse.generate(200);
    }

    @GetMapping("/admin/{page}")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> adminList(@PathVariable("page") Integer page) {
        return GeneralResponse.generate(200, depositService.creditsLogList(page));
    }
}
