package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Credits.DepositAdminForm;
import com.rabbit.backend.Bean.Credits.DepositSubmitForm;
import com.rabbit.backend.Service.DepositService;
import com.rabbit.backend.Service.FrequentService;
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
    private FrequentService frequentService;

    @Autowired
    public DepositController(DepositService depositService, FrequentService frequentService) {
        this.depositService = depositService;
        this.frequentService = frequentService;
    }

    @PostMapping("/pay")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> pay(@RequestBody @Valid DepositSubmitForm form, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        String key = "pay:deposit:" + uid;

        if (!frequentService.check(key, 10 * 60)) {
            return GeneralResponse.generate(400, "Pay too frequently, try again after 10 minutes.");
        }
        String cid = depositService.submitDeposit(form, uid);
        return GeneralResponse.generate(200, cid);
    }

    @PostMapping("/admin")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> adminDecide(@RequestBody @Valid DepositAdminForm form) {
        depositService.setDeposit(form.getCid(), form.getStatus());
        return GeneralResponse.generate(200);
    }

    @GetMapping("/admin/{page}")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> adminList(@PathVariable("page") Integer page) {
        return GeneralResponse.generate(200, depositService.creditsLogList(page));
    }
}
