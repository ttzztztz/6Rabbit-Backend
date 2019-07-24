package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Notification.NotificationListResponse;
import com.rabbit.backend.Service.NotificationService;
import com.rabbit.backend.Utilities.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notification")
@PreAuthorize("hasAuthority('User')")
public class NotificationController {
    private NotificationService service;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.service = notificationService;
    }

    @DeleteMapping("/item/{nid}")
    public Map<String, Object> deleteOne(@PathVariable("nid") String nid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (service.deleteOne(nid, uid)) {
            return GeneralResponse.generator(200);
        } else {
            return GeneralResponse.generator(403, "Permission denied.");
        }
    }

    @PostMapping("/item/{nid}")
    public Map<String, Object> readOne(@PathVariable("nid") String nid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (service.setOneRead(nid, uid)) {
            return GeneralResponse.generator(200);
        } else {
            return GeneralResponse.generator(403, "Permission denied.");
        }
    }

    @GetMapping("/all/{page}")
    public Map<String, Object> list(@PathVariable("page") Integer page, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        NotificationListResponse response = new NotificationListResponse();
        response.setCount(service.count(uid));
        response.setList(service.list(uid, page));
        return GeneralResponse.generator(200, response);
    }

    @PostMapping("/all")
    public Map<String, Object> readAll(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        service.setAllRead(uid);
        return GeneralResponse.generator(200);
    }

    @DeleteMapping("/all")
    public Map<String, Object> deleteAll(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        service.deleteAll(uid);
        return GeneralResponse.generator(200);
    }

    @GetMapping("/all")
    public Map<String, Object> count(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return GeneralResponse.generator(200, service.count(uid));
    }
}
