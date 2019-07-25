package com.rabbit.backend.Bean.Attach;

import lombok.Data;

import java.util.Date;

@Data
public class ThreadAttach {
    private String aid;
    private Integer fileSize;
    private Integer downloads;
    private String originalName;
    private Date createDate;
}
