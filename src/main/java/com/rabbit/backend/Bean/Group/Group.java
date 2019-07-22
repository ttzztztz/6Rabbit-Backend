package com.rabbit.backend.Bean.Group;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component("group")
@Data
public class Group {
    private Integer gid;
    private String name;
    private Boolean isAdmin;
}
