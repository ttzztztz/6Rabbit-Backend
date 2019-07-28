package com.rabbit.backend.Bean.Thread;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode(callSuper = true)
@Component
public class ThreadListItem extends Thread {
    private String fid;
}
