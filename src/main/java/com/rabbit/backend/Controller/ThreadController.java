package com.rabbit.backend.Controller;

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
import java.util.Map;

@RestController
@RequestMapping("/thread")
public class ThreadController {
    private ThreadService threadService;
    private PostService postService;
    private NotificationService notificationService;
    private AttachService attachService;
    private RuleService ruleService;
    private PayService payService;
    private FrequentService frequentService;

    @Autowired
    public ThreadController(ThreadService threadService, PostService postService, NotificationService notificationService,
                            AttachService attachService, RuleService ruleService, PayService payService, FrequentService frequentService) {
        this.threadService = threadService;
        this.postService = postService;
        this.notificationService = notificationService;
        this.attachService = attachService;
        this.ruleService = ruleService;
        this.payService = payService;
        this.frequentService = frequentService;
    }

    @GetMapping("/list/{fid}/{page}")
    public Map<String, Object> list(@PathVariable("fid") String fid, @PathVariable("page") Integer page) {
        ThreadListResponse threadListResponse = new ThreadListResponse();
        threadListResponse.setForum(threadService.forum(fid));
        threadListResponse.setList(threadService.list(fid, page));
        return GeneralResponse.generate(200, threadListResponse);
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
    public Map<String, Object> info(@PathVariable("tid") String tid, @PathVariable("page") Integer page,
                                    Authentication authentication) {
        String uid = authentication == null ? null : (String) authentication.getPrincipal();

        ThreadInfoResponse response = new ThreadInfoResponse();
        ThreadItem threadItem = threadService.info(tid);
        response.setThread(threadItem);
        response.setPostList(postService.list(tid, page));

        Post firstPost = postService.firstPost(tid);
        response.setFirstPost(firstPost);
        response.setAttachList(attachService.threadList(tid));

        if (threadItem.getCreditsType() != 0 && threadItem.getCredits() != 0
                && (uid == null || payService.userThreadNeedPay(uid, tid))) {
            firstPost.setMessage("");
            response.setNeedBuy(true);
        } else {
            response.setNeedBuy(false);
        }

        return GeneralResponse.generate(200, response);
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
        ruleService.applyRule(uid, "DeleteThread");
        return GeneralResponse.generate(200);
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@Valid @RequestBody ThreadEditorForm form, Errors errors,
                                      Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();
        String key = "thread:create:" + uid;
        if (!frequentService.check(key, 30)) {
            return GeneralResponse.generate(400, "Post too frequent, try again after 30 seconds.");
        }
        String tid = threadService.insert(uid, form);

        attachService.batchAttachThread(form.getAttach(), tid, uid);
        ruleService.applyRule(uid, "CreateThread");
        return GeneralResponse.generate(200, tid);
    }

    @PostMapping("/reply/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> create(@PathVariable("tid") String tid, @Valid @RequestBody PostEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }
        String uid = (String) authentication.getPrincipal();
        ThreadItem threadItem = threadService.info(tid);
        if (threadItem.getIsClosed()
                && !CheckAuthority.hasAuthority(authentication, "Admin")
        ) {
            return GeneralResponse.generate(400, "Thread already closed.");
        }

        if (threadItem.getCreditsType() != 0 && threadItem.getCredits() != 0 && payService.userThreadNeedPay(uid, tid)) {
            return GeneralResponse.generate(403, "Purchase thread first.");
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
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> update(@PathVariable("tid") String tid, @Valid @RequestBody ThreadEditorForm form,
                                      Errors errors, Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generate(500, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();
        if (!threadService.uid(tid).equals(uid)
                && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "Permission denied.");
        }

        attachService.batchAttachThread(form.getAttach(), tid, uid);
        threadService.update(tid, form.getFid(), form.getSubject(), form.getMessage());
        return GeneralResponse.generate(200);
    }

    @GetMapping("/pay/{tid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> pay(@PathVariable("tid") String tid, Authentication authentication) {
        String buyerUid = (String) authentication.getPrincipal();
        String sellerUid = threadService.uid(tid);
        if (payService.userThreadNeedPay(buyerUid, tid)) {
            if (payService.purchaseThread(buyerUid, sellerUid, tid)) {
                return GeneralResponse.generate(200);
            } else {
                return GeneralResponse.generate(400, "Credits Not Enough.");
            }
        } else {
            return GeneralResponse.generate(200);
        }
    }

    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> batchDelete(@Valid @RequestBody BatchDeleteForm form) {
        threadService.batchDelete(form.getTid());
        return GeneralResponse.generate(200);
    }
}
