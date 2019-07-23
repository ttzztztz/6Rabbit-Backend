package com.rabbit.backend.Bean.Forum;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Forum {
    private String fid;
    private String name;
    private String description;
    private Integer rank;
}
