package com.rabbit.backend.Bean.User;

import com.rabbit.backend.Bean.Thread.ThreadListItem;
import lombok.Data;

import java.util.List;

@Data
public class UserPurchasedListResponse {
    private Integer count;
    private List<ThreadListItem> list;
}
