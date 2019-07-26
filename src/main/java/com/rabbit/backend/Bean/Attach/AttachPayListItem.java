package com.rabbit.backend.Bean.Attach;

import com.rabbit.backend.Bean.Thread.ThreadListItem;
import lombok.Data;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
@Data
public class AttachPayListItem {
    private String aid;
    private ThreadListItem thread;
    private Integer fileSize;
    private Integer downloads;
    private String fileName;
    private String originalName;
    private Date createDate;
}
