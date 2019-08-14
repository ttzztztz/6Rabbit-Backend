package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class SearchItem extends UserPost {
    private String uid;
    private String username;
}
