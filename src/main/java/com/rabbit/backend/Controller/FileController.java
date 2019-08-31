package com.rabbit.backend.Controller;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachUploadForm;
import com.rabbit.backend.Security.CheckAuthority;
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
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
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

    @Value("${rabbit.limit.MAX_UNUSED_ATTACH_PER_USER}")
    private Integer maxUnusedAttachCountPerUser;

    @GetMapping("/avatar/{uid}")
    public void getAvatar(HttpServletResponse response, @PathVariable("uid") String uid) throws IOException {
        File file = new File(fileService.avatarPath(uid));
        if (!file.exists()) {
            Resource defaultAvatar = new ClassPathResource("static/default.avatar");
            file = defaultAvatar.getFile();
        }
        try (InputStream fstream = new FileInputStream(file);
             OutputStream ostream = response.getOutputStream()) {
            fileService.downloadFileByStream(fstream, ostream, file.length(), response);
        }
    }

    @PostMapping("/avatar")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> uploadAvatar(Part avatar, Authentication authentication) throws IOException {
        if (avatar.getSize() > 200 * 1024L) {
            avatar.delete();
            return GeneralResponse.generate(404, "Avatar should smaller than 200KB!");
        }

        String uid = (String) authentication.getPrincipal();
        File file = new File(fileService.avatarPath(uid));
        try (InputStream avatarInputStream = avatar.getInputStream();
             OutputStream fileOutputStream = new FileOutputStream(file)) {

            IOUtils.copy(avatarInputStream, fileOutputStream);
            avatar.delete();
            return GeneralResponse.generate(200);
        } catch (IOException e) {
            return GeneralResponse.generate(500, e.getMessage());
        }
    }

    @DeleteMapping("/avatar/{uid}")
    @PreAuthorize("hasAuthority('Admin')")
    public Map<String, Object> deleteAvatar(@PathVariable("uid") String uid) {
        File file = new File(fileService.avatarPath(uid));
        boolean result = true;

        if (file.exists()) {
            result = file.delete();
        }

        if (result) {
            return GeneralResponse.generate(200);
        } else {
            return GeneralResponse.generate(500);
        }
    }

    @PostMapping("/update/{aid}")
    @PreAuthorize("hasAuthority('User')")
    public Map<String, Object> update(Part attach, @PathVariable("aid") String aid, Authentication authentication) {
        String uid = (String) authentication.getPrincipal();

        String attachUid = attachService.uid(aid);
        if (!attachUid.equals(uid) && !CheckAuthority.hasAuthority(authentication, "Admin")) {
            return GeneralResponse.generate(403, "Permission denied.");
        }

        try {
            String path = fileService.attachPath(uid);
            File file = new File(path);
            try (InputStream avatarInputStream = attach.getInputStream();
                 OutputStream fileOutputStream = new FileOutputStream(file)) {
                IOUtils.copy(avatarInputStream, fileOutputStream);
                attach.delete();
            }

            Attach oldAttach = attachService.find(aid);
            String oldPath = attachService.getRealPath(oldAttach.getOriginalName());
            File oldFile = new File(oldPath);
            if (oldFile.exists()) {
                oldFile.delete();
            }

            AttachUploadForm attachUploadForm = new AttachUploadForm();
            attachUploadForm.setAid(aid);
            attachUploadForm.setFileName(fileService.getSQLPath(path));
            attachUploadForm.setFileSize(((Long) file.length()).intValue());
            attachUploadForm.setOriginalName(attach.getSubmittedFileName());
            attachService.updateAttach(attachUploadForm);

            return GeneralResponse.generate(200, attachUploadForm);
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
            try (InputStream avatarInputStream = attach.getInputStream();
                 OutputStream fileOutputStream = new FileOutputStream(file)) {
                IOUtils.copy(avatarInputStream, fileOutputStream);
                attach.delete();
            }

            AttachUploadForm attachUploadForm = new AttachUploadForm();
            attachUploadForm.setUid(uid);
            attachUploadForm.setFileName(fileService.getSQLPath(path));
            attachUploadForm.setFileSize(((Long) file.length()).intValue());
            attachUploadForm.setOriginalName(attach.getSubmittedFileName());
            attachService.insert(attachUploadForm);

            return GeneralResponse.generate(200, attachUploadForm);
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
        if (uid == null || !payService.userAttachDownloadAccess(uid, attach)) {
            response.setStatus(403);
            return;
        }

        File file = new File(attachService.getRealPath(attach.getFileName()));
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            ruleService.applyRule(uid, "DownloadAttach");

            String encodedAttachName = URLEncoder.encode(attach.getOriginalName(), StandardCharsets.UTF_8);
            response.setHeader("Content-Disposition", "attachment;filename=\"" + encodedAttachName + "\";" +
                    "filename*=utf-8''" + encodedAttachName);
            try (InputStream fstream = new FileInputStream(file);
                 OutputStream ostream = response.getOutputStream()) {
                fileService.downloadFileByStream(fstream, ostream, file.length(), response);
                attachService.incrementDownloads(aid);
            }
        }
    }

    @GetMapping("/picture/{aid}")
    public void picturePreview(@PathVariable("aid") String aid, HttpServletResponse response)
            throws IOException {
        Attach attach = attachService.find(aid);
        if (attach == null) {
            response.setStatus(404);
            return;
        }

        String[] fileOriginalNameSplitResult = attach.getOriginalName().split("\\.");
        String fileOriginalNameSuffix = fileOriginalNameSplitResult[fileOriginalNameSplitResult.length - 1].toLowerCase();
        String[] allowSuffixes = {"bmp", "jpg", "jpeg", "png", "gif"};
        boolean findResult = Arrays.asList(allowSuffixes).contains(fileOriginalNameSuffix);

        if (!findResult) {
            response.setStatus(400);
            return;
        }
        File file = new File(attachService.getRealPath(attach.getFileName()));
        if (!file.exists()) {
            response.setStatus(404);
        } else {
            response.setHeader("Content-Disposition", "attachment;filename=" + attach.getOriginalName());
            try (InputStream fstream = new FileInputStream(file);
                 OutputStream ostream = response.getOutputStream()) {
                fileService.downloadFileByStream(fstream, ostream, file.length(), response);
            }
        }
    }
}
