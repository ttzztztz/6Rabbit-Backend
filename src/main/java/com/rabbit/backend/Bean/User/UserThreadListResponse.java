package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Thread.ThreadListItem;
import lombok.Data;

import java.util.List;

@Data
public class UserThreadListResponse {
    private Integer threads;
    private List<ThreadListItem> list;
}
