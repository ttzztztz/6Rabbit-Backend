package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadListItem extends Thread {
    private String fid;
}
