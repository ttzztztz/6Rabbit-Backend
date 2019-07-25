package com.rabbit.backend.Service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

@Service
public class FileService {
    @Value("${rabbit.path}")
    private String basePath;

    public String avatarPath(String uid) {
        return basePath + "avatar/" + uid + ".avatar";
    }

    public String attachPath(String uid) throws IOException {
        Date date = new Date();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");

        Random random = new Random();
        int randomNumber = random.nextInt(10000);
        String path = basePath + "attach/" + dateFormat.format(date);
        String fileName = uid + "_" + System.currentTimeMillis() + "_" + randomNumber + ".file";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException();
            }
        }
        return path + "/" + fileName;
    }

    public void downloadFileByStream(InputStream fstream, OutputStream ostream
            , Long contentLength, HttpServletResponse response) throws IOException {
        response.setContentLengthLong(contentLength);
        response.setContentType("application/octet-stream");
        response.setStatus(200);
        IOUtils.copy(fstream, ostream);
        response.flushBuffer();
    }
}
