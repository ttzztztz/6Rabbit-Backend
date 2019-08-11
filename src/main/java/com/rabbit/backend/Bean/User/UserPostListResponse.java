package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Thread.UserPost;
import lombok.Data;

import java.util.List;

@Data
public class UserPostListResponse {
    private Integer posts;
    private List<UserPost> list;
}
