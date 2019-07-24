package com.rabbit.backend.Bean.Attach;

import lombok.Data;

@Data
public class AttachUpload {
    private String aid;

    private String uid;
    private Integer fileSize;
    private String fileName;
    private String originalName;
}
