package com.rabbit.backend.Controller;

import com.rabbit.backend.Service.ForumService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/forum")
public class ForumController {
    private ForumService forumService;

    @Autowired
    public ForumController(ForumService forumService) {
        this.forumService = forumService;
    }

    @GetMapping("/list")
    public Map<String, Object> list() {
        return GeneralResponse.generate(200, forumService.list());
    }
}
