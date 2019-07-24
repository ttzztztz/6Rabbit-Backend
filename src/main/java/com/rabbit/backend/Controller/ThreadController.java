package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Thread.*;
import com.rabbit.backend.Service.NotificationService;
import com.rabbit.backend.Service.PostService;
import com.rabbit.backend.Service.ThreadService;
import com.rabbit.backend.Utilities.FieldErrorResponse;
import com.rabbit.backend.Utilities.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private ThreadService threadService;
    private PostService postService;
    private NotificationService notificationService;

    @Autowired
    public ThreadController(ThreadService threadService, PostService postService, NotificationService notificationService) {
        this.threadService = threadService;
        this.postService = postService;
        this.notificationService = notificationService;
    }

    @GetMapping("/list/{fid}/{page}")
    public Map<String, Object> list(@PathVariable("fid") String fid, @PathVariable("page") Integer page) {
        ThreadListResponse threadListResponse = new ThreadListResponse();
        threadListResponse.setForum(threadService.forum(fid));
        threadListResponse.setList(threadService.list(fid, page));
        return GeneralResponse.generator(200, threadListResponse);
    }

    @PostMapping("/digest")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setDigest(@Valid @RequestBody ThreadDigestForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "digest", form.getDigest().toString());
        return GeneralResponse.generator(200);
    }

    @PostMapping("/top")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setTop(@Valid @RequestBody ThreadTopForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isTop", form.getTop().toString());
        return GeneralResponse.generator(200);
    }

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setClosed(@Valid @RequestBody ThreadCloseForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isClosed", form.getClose().toString());
        return GeneralResponse.generator(200);
    }

    @GetMapping("/{tid}/{page}")
    public Map<String, Object> info(@PathVariable("tid") String tid, @PathVariable("page") Integer page) {
        ThreadInfoResponse response = new ThreadInfoResponse();

        response.setThread(threadService.info(tid));
        response.setPostList(postService.list(tid, page));
        response.setFirstPost(postService.firstPost(tid));

        return GeneralResponse.generator(200, response);
    }

    @DeleteMapping("/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("tid") String tid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);

        if (threadItem == null) {
            return GeneralResponse.generator(404, "Thread doesn't exist.");
        }

        if (!threadItem.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(403, "Permission denied.");
        }
        threadService.delete(tid);
        return GeneralResponse.generator(200);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@Valid @RequestBody ThreadEditorForm form, Errors errors,
                                      Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();
        String tid = threadService.insert(uid, form);
        return GeneralResponse.generator(200, tid);
    }

    @PostMapping("/reply/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@PathVariable("tid") String tid, @Valid @RequestBody PostEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);
        if (threadItem.getIsClosed()
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(400, "Thread already closed.");
        }

        threadService.reply(tid, form, uid);
        notificationService.push(uid, threadItem.getUser().getUid(),
                "有人回复了您的帖子《" + threadItem.getSubject() + "》！", "/thread/info/" + tid + "/1");
        return GeneralResponse.generator(200);
    }

    @PutMapping("/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> update(@PathVariable("tid") String tid, @Valid @RequestBody ThreadEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(500, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);
        if (!threadItem.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(403, "Permission denied.");
        }

        threadService.update(tid, form.getFid(), form.getSubject(), form.getMessage());
        return GeneralResponse.generator(200);
    }
}
