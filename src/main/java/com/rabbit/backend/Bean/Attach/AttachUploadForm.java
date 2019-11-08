package com.rabbit.backend.Bean.Attach;

import lombok.Data;

import java.util.Date;

@Data
public class AttachUploadForm {
    private String aid;

    private String uid;
    private Integer fileSize;
    private String fileName;
    private String originalName;

    private Date createDate;
}
