package com.rabbit.backend.Bean.Forum;

import lombok.Data;

@Data
public class Forum {
    private String fid;
    private String name;
    private String description;
    private Integer threads;
    private String type;
    private Boolean adminPost;
}
