package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import java.util.List;

@Data
public class ThreadInfoResponse {
    private ThreadItem thread;
    private List<Post> postList;
    private Post firstPost;
}
