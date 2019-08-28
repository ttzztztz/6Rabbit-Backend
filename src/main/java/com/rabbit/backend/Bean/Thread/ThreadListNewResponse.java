package com.rabbit.backend.Bean.Thread;

import lombok.Data;

import java.util.List;

@Data
public class ThreadListNewResponse<T extends ThreadListItem> {
    private Integer total;
    private List<T> list;
}
