package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.Forum.Forum;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.stereotype.Component;

@Data
@EqualsAndHashCode(callSuper = true)
@Component("thread_item")
public class ThreadItem extends Thread {
    private Forum forum;
}
