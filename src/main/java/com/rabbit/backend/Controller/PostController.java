package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.Bean.Thread.PostEditorForm;
import com.rabbit.backend.Security.CheckAuthority;
import com.rabbit.backend.Service.AttachService;
import com.rabbit.backend.Service.CaptchaService;
import com.rabbit.backend.Service.PostService;
import com.rabbit.backend.Service.RuleService;
import com.rabbit.backend.Utilities.Response.FieldErrorResponse;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;
    private RuleService ruleService;
    private CaptchaService captchaService;
    private AttachService attachService;

    @Autowired
    public PostController(PostService postService, RuleService ruleService, CaptchaService captchaService,
                          AttachService attachService) {
        this.postService = postService;
        this.ruleService = ruleService;
        this.captchaService = captchaService;
        this.attachService = attachService;
    }

    @DeleteMapping("/{pid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("pid") String pid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        if (!postService.uid(pid).equals(uid) && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "Permission denied.");
        }

        Post post = postService.find(pid);
        if (post.getIsFirst()) {
            return GeneralResponse.generate(404, "Bad request.");
        }

        postService.delete(pid);
        ruleService.applyRule(uid, "DeleteThread");
        return GeneralResponse.generate(200);
    }

    @GetMapping("/{pid}")
    public Map<String, Object> getInfo(@PathVariable("pid") String pid) {
        Post post = postService.find(pid);
        post.setAttachList(attachService.listWithoutUser(pid));
        return GeneralResponse.generate(200, post);
    }

    @PutMapping("/{pid}")
    @PreAuthorize("hasAuthority('canPost')")
    public Map<String, Object> update(@PathVariable("pid") String pid, @Valid @RequestBody PostEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        captchaService.verifyToken(form.getToken());

        if (!postService.uid(pid).equals(authentication.getPrincipal())
                && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "Permission denied.");
        }

        String tid = postService.tid(pid);
        String uid = (String) authentication.getPrincipal();
        postService.update(pid, form.getMessage());

        if (form.getAttach() != null && form.getAttach().size() > 0) {
            attachService.batchAttachThread(form.getAttach(), tid, pid, uid);
        }
        return GeneralResponse.generate(200, tid);
    }
}
