package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Attach.UploadResponse;
import com.rabbit.backend.Service.FileService;
import com.rabbit.backend.Utilities.GeneralResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.servlet.http.Part;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.Vector;

@RestController
@RequestMapping("/file")
public class FileController {
    private FileService fileService;

    @Autowired
    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping("/avatar/{uid}")
    public void getAvatar(HttpServletResponse response, @PathVariable("uid") String uid) throws IOException {
        File file = new File(fileService.avatarPath(uid));
        if (!file.exists()) {
            Resource defaultAvatar = new ClassPathResource("static/default.avatar");
            file = defaultAvatar.getFile();
        }

        InputStream fstream = new FileInputStream(file);
        OutputStream ostream = response.getOutputStream();
        response.setContentLengthLong(file.length());
        response.setContentType("application/octet-stream");
        IOUtils.copy(fstream, ostream);
        response.flushBuffer();
        fstream.close();
        ostream.close();
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> uploadAvatar(Part avatar, Authentication authentication, HttpSession session) {
        String uid = (String) authentication.getPrincipal();
        try {
            File file = new File(fileService.avatarPath(uid));
            InputStream avatarInputStream = avatar.getInputStream();
            OutputStream fileOutputStream = new FileOutputStream(file);
            IOUtils.copy(avatarInputStream, fileOutputStream);

            avatarInputStream.close();
            fileOutputStream.close();
            avatar.delete();
            return GeneralResponse.generator(200);
        } catch (IOException e) {
            return GeneralResponse.generator(500, e.getMessage());
        }
    }

    @PostMapping("/file")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> upload(Part file) {
        List<UploadResponse> responseVector = new Vector<>();
        // todo: some logic ...
        return GeneralResponse.generator(200, responseVector);
    }
}
