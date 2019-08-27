package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
@Data
public class ThreadListImageItem extends ThreadListItem {
    private String image;
}
