package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.Attach.ThreadAttach;
import lombok.Data;

import java.util.List;

@Data
public class ThreadInfoResponse {
    private ThreadItem thread;
    private List<Post> postList;
    private Post firstPost;
    private List<ThreadAttach> attachList;
    private Boolean needBuy;
}
