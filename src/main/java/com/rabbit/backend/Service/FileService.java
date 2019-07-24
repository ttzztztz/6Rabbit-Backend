package com.rabbit.backend.Service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FileService {
    @Value("${rabbit.path}")
    private String basePath;

    public String avatarPath(String uid) {
        return basePath + "avatar/" + uid + ".avatar";
    }

    public String attachPath(String uid) {
        return basePath + "attach/" + uid + ".file";
    }
}
