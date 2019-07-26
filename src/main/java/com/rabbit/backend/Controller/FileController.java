package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachUpload;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Service.AttachService;
import com.rabbit.backend.Service.FileService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Part;
import java.io.*;
import java.util.Map;

@RestController
@RequestMapping("/file")
public class FileController {
    private FileService fileService;
    private AttachService attachService;

    @Autowired
    public FileController(FileService fileService, AttachService attachService) {
        this.fileService = fileService;
        this.attachService = attachService;
    }

    @Value("${rabbit.limit.max-unused-attach-per-user}")
    private Integer maxUnusedAttachCountPerUser;

    @GetMapping("/avatar/{uid}")
    public void getAvatar(HttpServletResponse response, @PathVariable("uid") String uid) throws IOException {
        File file = new File(fileService.avatarPath(uid));
        if (!file.exists()) {
            Resource defaultAvatar = new ClassPathResource("static/default.avatar");
            file = defaultAvatar.getFile();
        }

        InputStream fstream = new FileInputStream(file);
        OutputStream ostream = response.getOutputStream();
        fileService.downloadFileByStream(fstream, ostream, file.length(), response);
        fstream.close();
        ostream.close();
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> uploadAvatar(Part avatar, Authentication authentication) {
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

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> upload(Part attach, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        Integer unusedAttaches = attachService.userUnusedAttachCount(uid);
        if (unusedAttaches >= maxUnusedAttachCountPerUser) {
            return GeneralResponse.generator(400, "User max unused attach count exceed.");
        }

        try {
            String path = fileService.attachPath(uid);
            File file = new File(path);
            InputStream avatarInputStream = attach.getInputStream();
            OutputStream fileOutputStream = new FileOutputStream(file);
            IOUtils.copy(avatarInputStream, fileOutputStream);
            avatarInputStream.close();
            fileOutputStream.close();
            attach.delete();

            AttachUpload attachUpload = new AttachUpload();
            attachUpload.setUid(uid);
            attachUpload.setFileName(path);
            attachUpload.setFileSize(((Long) file.length()).intValue());
            attachUpload.setOriginalName(attach.getSubmittedFileName());
            String aid = attachService.insert(attachUpload);
            return GeneralResponse.generator(200, aid);
        } catch (IOException e) {
            return GeneralResponse.generator(500, e.getMessage());
        }
    }

    @PostMapping("/download/{aid}")
    public void attachDownload(@PathVariable("aid") String aid, String token, HttpServletResponse response)
            throws IOException {
        if (JWTUtils.verify(token) == null) {
            response.setStatus(403);
            return;
        }

        Attach attach = attachService.find(aid);
        if (attach == null || attach.getTid() == null) {
            response.setStatus(404);
            return;
        }
        File file = new File(attach.getFileName());
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            InputStream fstream = new FileInputStream(file);
            OutputStream ostream = response.getOutputStream();
            fileService.downloadFileByStream(fstream, ostream, file.length(), response);
            fstream.close();
            ostream.close();
        }
    }
}
