package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Thread.Post;
import com.rabbit.backend.Bean.Thread.PostEditorForm;
import com.rabbit.backend.Service.PostService;
import com.rabbit.backend.Utilities.FieldErrorResponse;
import com.rabbit.backend.Utilities.GeneralResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/post")
public class PostController {
    private PostService postService;

    @Autowired
    public PostController(PostService postService) {
        this.postService = postService;
    }

    @DeleteMapping("/{pid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> delete(@PathVariable("pid") String pid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        Post post = postService.find(pid);

        if (post == null) {
            return GeneralResponse.generator(-1, "Post doesn't exist.");
        }

        if (!post.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(-1, "Permission denied.");
        }
        postService.delete(pid);
        return GeneralResponse.generator(1);
    }

    @PutMapping("/{pid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> update(@PathVariable("pid") String pid, @RequestBody PostEditorForm form, Errors errors,
                                      Authentication authentication) {
        if (errors.hasErrors()) {
            return GeneralResponse.generator(-1, FieldErrorResponse.generator(errors));
        }

        String uid = (String) authentication.getPrincipal();
        Post post = postService.find(pid);

        if (post == null) {
            return GeneralResponse.generator(-1, "Post doesn't exist.");
        }

        if (!post.getUser().getUid().equals(uid)
                && !authentication.getAuthorities().contains("Admin")
        ) {
            return GeneralResponse.generator(-1, "Permission denied.");
        }

        postService.update(pid, form.getContent());
        return GeneralResponse.generator(1);
    }
}
