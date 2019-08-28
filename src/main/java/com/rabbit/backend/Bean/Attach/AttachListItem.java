package com.rabbit.backend.Bean.Attach;

import com.rabbit.backend.Bean.Thread.ThreadListItem;
import lombok.Data;

import java.util.Date;

// for each user to see his purchased attach list
// therefore, no user item inside this Bean

@Data
public class AttachListItem {
    private String aid;
    private ThreadListItem thread;
    private Integer fileSize;
    private Integer downloads;
    private String fileName;
    private String originalName;
    private Date createDate;
}
