package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.Forum.Forum;
import lombok.Data;

import java.util.List;

@Data
public class ThreadListResponse<T extends ThreadListItem> {
    private Forum forum;
    private List<T> list;
}
