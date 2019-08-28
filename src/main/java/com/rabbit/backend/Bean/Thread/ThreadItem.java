package com.rabbit.backend.Bean.Thread;

import com.rabbit.backend.Bean.Forum.Forum;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class ThreadItem extends Thread {
    private Forum forum;
}
