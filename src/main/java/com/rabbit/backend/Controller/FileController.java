package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachUpload;
import com.rabbit.backend.Security.JWTUtils;
import com.rabbit.backend.Service.AttachService;
import com.rabbit.backend.Service.FileService;
import com.rabbit.backend.Service.PayService;
import com.rabbit.backend.Service.RuleService;
import com.rabbit.backend.Utilities.Response.GeneralResponse;
import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private PayService payService;
    private RuleService ruleService;

    @Autowired
    public FileController(FileService fileService, AttachService attachService, PayService payService,
                          RuleService ruleService) {
        this.fileService = fileService;
        this.attachService = attachService;
        this.payService = payService;
        this.ruleService = ruleService;
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
            return GeneralResponse.generate(200);
        } catch (IOException e) {
            return GeneralResponse.generate(500, e.getMessage());
        }
    }

    @PostMapping("/upload")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> upload(Part attach, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();
        Integer unusedAttaches = attachService.userUnusedAttachCount(uid);
        if (unusedAttaches >= maxUnusedAttachCountPerUser) {
            return GeneralResponse.generate(400, "User max unused attach count exceed.");
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
            return GeneralResponse.generate(200, aid);
        } catch (IOException e) {
            return GeneralResponse.generate(500, e.getMessage());
        }
    }

    @PostMapping("/download/{aid}")
    public void attachDownload(@PathVariable("aid") String aid, String token, HttpServletResponse response)
            throws IOException {
        UsernamePasswordAuthenticationToken jwt = JWTUtils.verify(token);
        if (jwt == null) {
            response.setStatus(403);
            return;
        }
        String uid = (String) jwt.getPrincipal();
        Attach attach = attachService.find(aid);

        if (attach == null || attach.getTid() == null) {
            response.setStatus(404);
            return;
        }
        if (!payService.userAttachDownloadAccess(uid, attach, attach.getTid())) {
            response.setStatus(403);
            return;
        }

        File file = new File(attach.getFileName());
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            ruleService.applyRule(uid, "DownloadAttach");
            response.setHeader("Content-Disposition", "attachment;filename=" + attach.getOriginalName());
            InputStream fstream = new FileInputStream(file);
            OutputStream ostream = response.getOutputStream();
            fileService.downloadFileByStream(fstream, ostream, file.length(), response);
            fstream.close();
            ostream.close();
        }
    }

    @GetMapping("/picture/{aid}")
    public void picturePreview(@PathVariable("aid") String aid, HttpServletResponse response)
            throws IOException {
        Attach attach = attachService.find(aid);
        if (attach == null || attach.getTid() == null) {
            response.setStatus(404);
            return;
        }

        String[] fileOriginalNameSplitResult = attach.getOriginalName().split(".");
        String fileOriginalNameSuffix = fileOriginalNameSplitResult[fileOriginalNameSplitResult.length - 1].toLowerCase();
        String[] allowSuffixes = {"bmp", "jpg", "jpeg", "png", "gif"};
        boolean findResult = false;
        for (String allowSuffix : allowSuffixes) {
            if (allowSuffix.equals(fileOriginalNameSuffix)) {
                findResult = true;
                break;
            }
        }

        if (!findResult) {
            response.setStatus(400);
            return;
        }
        File file = new File(attach.getFileName());
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + attach.getOriginalName());
            InputStream fstream = new FileInputStream(file);
            OutputStream ostream = response.getOutputStream();
            fileService.downloadFileByStream(fstream, ostream, file.length(), response);
            fstream.close();
            ostream.close();
        }
    }
}
