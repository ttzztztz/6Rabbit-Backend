package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Notification.NotificationListResponse;
import com.rabbit.backend.Service.NotificationService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/notification")
@PreAuthorize("hasAuthority('User')")
public class NotificationController {
    private NotificationService notificationService;

    @Autowired
    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @DeleteMapping("/item/{nid}")
    public Map<String, Object> deleteOne(@PathVariable("nid") String nid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (notificationService.deleteOne(nid, uid)) {
            return GeneralResponse.generator(200);
        } else {
            return GeneralResponse.generator(403, "Permission denied.");
        }
    }

    @PostMapping("/item/{nid}")
    public Map<String, Object> readOne(@PathVariable("nid") String nid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (notificationService.setOneRead(nid, uid)) {
            return GeneralResponse.generator(200);
        } else {
            return GeneralResponse.generator(403, "Permission denied.");
        }
    }

    @GetMapping("/all/{page}")
    public Map<String, Object> list(@PathVariable("page") Integer page, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        NotificationListResponse response = new NotificationListResponse();
        response.setCount(notificationService.count(uid));
        response.setList(notificationService.list(uid, page));
        return GeneralResponse.generator(200, response);
    }

    @PostMapping("/all")
    public Map<String, Object> readAll(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        notificationService.setAllRead(uid);
        return GeneralResponse.generator(200);
    }

    @DeleteMapping("/all")
    public Map<String, Object> deleteAll(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        notificationService.deleteAll(uid);
        return GeneralResponse.generator(200);
    }

    @GetMapping("/all")
    public Map<String, Object> count(Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        return GeneralResponse.generator(200, notificationService.count(uid));
    }
}
