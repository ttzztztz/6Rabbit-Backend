package com.rabbit.backend.Service;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
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
        String path = dateFormat.format(date);
        String fileName = uid + "_" + System.currentTimeMillis() + "_" + randomNumber + ".file";
        File file = new File(path);
        if (!file.exists()) {
            if (!file.mkdirs()) {
                throw new IOException();
            }
        }
        return path + "/" + fileName;
    }

    public void downloadFileByStream(InputStream fstream, OutputStream ostream,
                                     Long contentLength, HttpServletResponse response) throws IOException {
        response.setContentLengthLong(contentLength);
        response.setContentType("application/octet-stream");
        response.setStatus(200);
        IOUtils.copy(fstream, ostream);
        response.flushBuffer();
    }

    @Async
    public void downloadRemoteFile(String filePath, String url) throws IOException {
        File tempFile = new File(filePath + ".tmp");
        URLConnection connection = new URL(url).openConnection();

        try (OutputStream ostream = new FileOutputStream(tempFile);
             InputStream istream = connection.getInputStream()) {

            IOUtils.copy(istream, ostream);

            File realFile = new File(filePath);
            if (tempFile.exists() && tempFile.length() <= 512 * 1024) {
                try (InputStream ifstream = new FileInputStream(tempFile);
                     OutputStream ofstream = new FileOutputStream(realFile);) {
                    IOUtils.copy(ifstream, ofstream);
                }
            }
        } catch (IOException ex) {
            // do nothing ...
        } finally {
            tempFile.delete();
        }
    }
}
