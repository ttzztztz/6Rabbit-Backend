package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Attach.ThreadAttach;
import com.rabbit.backend.Bean.Forum.Forum;
import com.rabbit.backend.Bean.Thread.*;
import com.rabbit.backend.Security.CheckAuthority;
import com.rabbit.backend.Service.*;
import com.rabbit.backend.Utilities.Response.FieldErrorResponse;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private ThreadService threadService;
    private PostService postService;
    private NotificationService notificationService;
    private AttachService attachService;
    private RuleService ruleService;
    private FrequentService frequentService;
    private SEOService seoService;
    private ForumService forumService;
    private CaptchaService captchaService;

    @Autowired
    public ThreadController(ThreadService threadService, PostService postService, NotificationService notificationService,
                            AttachService attachService, RuleService ruleService, FrequentService frequentService,
                            SEOService seoService, ForumService forumService, CaptchaService captchaService) {
        this.threadService = threadService;
        this.postService = postService;
        this.notificationService = notificationService;
        this.attachService = attachService;
        this.ruleService = ruleService;
        this.frequentService = frequentService;
        this.seoService = seoService;
        this.forumService = forumService;
        this.captchaService = captchaService;
    }

    @GetMapping("/list/{fid}/{page}")
    public Map<String, Object> list(@PathVariable("fid") String fid, @PathVariable("page") Integer page) {
        Forum forum = threadService.forum(fid);

        if (forum.getType().equals("image")) {
            ThreadListResponse<ThreadListImageItem> threadListResponse = new ThreadListResponse<>();

            threadListResponse.setForum(forum);
            threadListResponse.setList(threadService.listImage(fid, page));

            return GeneralResponse.generate(200, threadListResponse);
        } else {
            ThreadListResponse<ThreadListItem> threadListResponse = new ThreadListResponse<>();

            threadListResponse.setForum(forum);
            threadListResponse.setList(threadService.list(fid, page));

            return GeneralResponse.generate(200, threadListResponse);
        }
    }

    @GetMapping("/list/new/{page}")
    public Map<String, Object> listNew(@PathVariable("page") Integer page) {
        ThreadListNewResponse<ThreadListItem> threadListNewResponse = new ThreadListNewResponse<>();

        threadListNewResponse.setTotal(threadService.listNewCount());
        threadListNewResponse.setList(threadService.listNew(page));

        return GeneralResponse.generate(200, threadListNewResponse);
    }

    @PostMapping("/diamond")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setDiamond(@Valid @RequestBody ThreadDiamondForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "diamond", form.getDiamond().toString());
        return GeneralResponse.generate(200);
    }

    @PostMapping("/top")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setTop(@Valid @RequestBody ThreadTopForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isTop", form.getTop().toString());
        return GeneralResponse.generate(200);
    }

    @PostMapping("/close")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> setClosed(@Valid @RequestBody ThreadCloseForm form, Errors errors) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        threadService.modify(form.getTid(), "isClosed", form.getClose().toString());
        return GeneralResponse.generate(200);
    }

    @GetMapping("/{tid}/{page}")
    public Map<String, Object> info(@PathVariable("tid") String tid, @PathVariable("page") Integer page) {
        ThreadInfoResponse response = new ThreadInfoResponse();

        ThreadItem threadItem = threadService.info(tid);
        response.setThread(threadItem);
        Post firstPost = postService.firstPost(tid);
        response.setFirstPost(firstPost);
        List<ThreadAttach> threadAttachList = attachService.threadList(tid);
        response.setAttachList(threadAttachList);
        List<Post> postList = postService.list(tid, page);
        response.setPostList(postList);

        return GeneralResponse.generate(200, response);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('canPost')")
    public Map<String, Object> create(@Valid @RequestBody ThreadEditorForm form, Errors errors,
                                      Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        captchaService.verifyToken(form.getToken());

        String uid = (String) authentication.getPrincipal();
        String key = "thread:create:" + uid;
        if (!frequentService.check(key, 30)) {
            return GeneralResponse.generate(400, "Post too frequent, try again after 30 seconds.");
        }
        Forum forum = forumService.find(form.getFid());
        if (forum.getAdminPost() && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "No permission to post in this forum.");
        }
        String tid = threadService.insert(uid, form);

        attachService.batchAttachThread(form.getAttach(), tid, uid);
        ruleService.applyRule(uid, "CreateThread");

        if (CheckAuthority.hasAuthority(authentication, "Admin")) {
            seoService.pushToBaidu(tid);
        }
        return GeneralResponse.generate(200, tid);
    }

    @PostMapping("/reply/{tid}")
    @PreAuthorize("hasAuthority('canPost')")
    public Map<String, Object> create(@PathVariable("tid") String tid, @Valid @RequestBody PostEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        captchaService.verifyToken(form.getToken());

        String uid = (String) authentication.getPrincipal();
        ThreadListItem threadItem = threadService.findWithThreadListItem(tid);
        if (threadItem.getIsClosed() && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(400, "Thread already closed.");
        }

        String quotePid = form.getQuotepid();
        if (!quotePid.equals("0")) {
            Post quotePost = postService.find(quotePid);
            if (quotePost == null || !quotePost.getTid().equals(form.getTid())) {
                return GeneralResponse.generate(404, "Invalid quote.");
            }
        }

        String key = "thread:reply:" + uid;
        if (!frequentService.check(key, 5)) {
            return GeneralResponse.generate(400, "Reply too frequent, try again after 5 seconds.");
        }

        threadService.reply(tid, form, uid);
        if (!uid.equals(threadItem.getUser().getUid())) {
            notificationService.push(uid, threadItem.getUser().getUid(),
                    "有人回复了您的帖子《" + threadItem.getSubject() + "》！", "/thread/info/" + tid + "/1");
        }
        ruleService.applyRule(uid, "CreatePost");
        return GeneralResponse.generate(200);
    }

    @PutMapping("/{tid}")
    @PreAuthorize("hasAuthority('canPost')")
    public Map<String, Object> update(@PathVariable("tid") String tid, @Valid @RequestBody ThreadEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        captchaService.verifyToken(form.getToken());

        String uid = (String) authentication.getPrincipal();
        if (!threadService.uid(tid).equals(uid)
                && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "Permission denied.");
        }

        attachService.batchAttachThread(form.getAttach(), tid, uid);
        threadService.update(tid, form.getFid(), form.getSubject(), form.getMessage());
        return GeneralResponse.generate(200);
    }

    @DeleteMapping("/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("tid") String tid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);

        if (threadItem == null) {
            return GeneralResponse.generate(404, "Thread doesn't exist.");
        }

        if (!threadItem.getUser().getUid().equals(uid)
                && !CheckAuthority.hasAuthority(authentication, "Admin")
        ) {
            return GeneralResponse.generate(403, "Permission denied.");
        }
        attachService.deleteByTid(tid);
        threadService.delete(tid);
        ruleService.applyRule(threadItem.getUser().getUid(), "DeleteThread");
        return GeneralResponse.generate(200);
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> batchDelete(@Valid @RequestBody ThreadBatchDeleteForm form) {
        for (String tid : form.getTid()) {
            ThreadItem threadItem = threadService.info(tid);

            attachService.deleteByTid(tid);
            threadService.delete(tid);
            ruleService.applyRule(threadItem.getUser().getUid(), "DeleteThread");
        }
        return GeneralResponse.generate(200);
    }

    @PostMapping("/search/{page}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> search(@Valid @RequestBody SearchForm form, @PathVariable("page") Integer page) {
        SearchResponse searchResponse = new SearchResponse();
        searchResponse.setList(threadService.search(form.getKeywords(), page));
        searchResponse.setCount(threadService.searchCount(form.getKeywords()));
        return GeneralResponse.generate(200, searchResponse);
    }
}
