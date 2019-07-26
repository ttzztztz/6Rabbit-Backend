package com.rabbit.backend.Controller;

import com.rabbit.backend.Service.AttachService;
import com.rabbit.backend.Service.PayService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/attach")
@PreAuthorize("hasAuthority('User')")
public class AttachController {
    private AttachService attachService;
    private PayService payService;

    @Autowired
    public AttachController(AttachService attachService, PayService payService) {
        this.attachService = attachService;
        this.payService = payService;
    }

    @DeleteMapping("/{aid}")
    public Map<String, Object> delete(@PathVariable("aid") String aid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        String attachUid = attachService.uid(aid);
        if (!attachUid.equals(uid) && !authentication.getAuthorities().contains("Admin")) {
            return GeneralResponse.generator(403, "Permission denied.");
        }
        return GeneralResponse.generator(attachService.deleteByAid(aid) ? 200 : 400);
    }

    @GetMapping("/unused")
    public Map<String, Object> unused(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return GeneralResponse.generator(200, attachService.findUnused(uid));
    }

    @GetMapping("/pay/{aid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> pay(@PathVariable("aid") String aid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (payService.userAttachNeedPay(uid, aid)) {
            if (payService.purchaseAttach(uid, aid)) {
                return GeneralResponse.generator(200);
            } else {
                return GeneralResponse.generator(400, "Credits Not Enough.");
            }
        } else {
            return GeneralResponse.generator(200);
        }
    }
}
