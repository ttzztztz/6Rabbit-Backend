package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Thread.*;
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

    @Autowired
    public ThreadController(ThreadService threadService, PostService postService) {
        this.threadService = threadService;
        this.postService = postService;
    }

    @GetMapping("/list/{fid}/{page}")
    public Map<String, Object> list(@PathVariable("fid") String fid, @PathVariable("page") Integer page) {
        ThreadListResponse threadListResponse = new ThreadListResponse();
        threadListResponse.setForum(threadService.forum(fid));
        threadListResponse.setList(threadService.list(fid, page));
        return GeneralResponse.generator(1, threadListResponse);
    }

    @PostMapping("/digest")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setDigest(@Valid @RequestBody ThreadDigestForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "digest", form.getDigest().toString());
        return GeneralResponse.generator(1);
    }

    @PostMapping("/top")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setTop(@Valid @RequestBody ThreadTopForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isTop", form.getTop().toString());
        return GeneralResponse.generator(1);
    }

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setClosed(@Valid @RequestBody ThreadCloseForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isClosed", form.getClose().toString());
        return GeneralResponse.generator(1);
    }

    @GetMapping("/{tid}/{page}")
    public Map<String, Object> info(@PathVariable("tid") String tid, @PathVariable("page") Integer page) {
        ThreadInfoResponse response = new ThreadInfoResponse();

        response.setThread(threadService.info(tid));
        response.setPostList(postService.list(tid, page));
        response.setFirstPost(postService.firstPost(tid));

        return GeneralResponse.generator(1, response);
    }

    @DeleteMapping("/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("tid") String tid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);

        if (threadItem == null) {
            return GeneralResponse.generator(-1, "Thread doesn't exist.");
        }

        if (!threadItem.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(-1, "Permission denied.");
        }
        threadService.delete(tid);
        return GeneralResponse.generator(1);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@Valid @RequestBody ThreadEditorForm form, Authentication authentication,
                                      Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();
        String tid = threadService.insert(uid, form);
        return GeneralResponse.generator(1, tid);
    }

    @PostMapping("/reply/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@PathVariable("tid") String tid, @Valid @RequestBody PostEditorForm form,
                                      Authentication authentication, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);
        if (threadItem.getIsClosed()
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(-1, "Thread already closed.");
        }

        threadService.reply(tid, form, uid);
        return GeneralResponse.generator(1);
    }

    @PutMapping("/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> update(@PathVariable("tid") String tid, @Valid @RequestBody ThreadEditorForm form,
                                      Authentication authentication, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);
        if (!threadItem.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(-1, "Permission denied.");
        }

        threadService.update(tid, form.getFid(), form.getSubject(), form.getContent());
        return GeneralResponse.generator(1);
    }
}
