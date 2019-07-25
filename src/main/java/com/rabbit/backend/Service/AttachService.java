package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachUpload;
import com.rabbit.backend.Bean.Attach.ThreadAttach;
import com.rabbit.backend.DAO.AttachDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;

@Service
public class AttachService {
    private AttachDAO attachDAO;

    @Autowired
    public AttachService(AttachDAO attachDAO) {
        this.attachDAO = attachDAO;
    }

    private Boolean deleteByFile(File file, String aid) {
        if (file.exists() && file.delete()) {
            attachDAO.delete(aid);
            return true;
        } else {
            return false;
        }
    }

    public Boolean deleteByAid(String aid) {
        Attach attach = attachDAO.find(aid);
        File file = new File(attach.getFileName());
        return deleteByFile(file, aid);
    }

    private Boolean deleteByAttach(Attach attach) {
        File file = new File(attach.getFileName());
        return deleteByFile(file, attach.getAid());
    }

    public List<Attach> list(String tid) {
        return attachDAO.findByTid(tid);
    }

    public List<ThreadAttach> threadList(String tid) {
        return attachDAO.findByTidInThreadlist(tid);
    }

    @Transactional
    public Boolean deleteByTid(String tid) {
        List<Attach> attachList = attachDAO.findByTid(tid);
        int failedCount = 0;
        for (Attach attach : attachList) {
            if (!deleteByAttach(attach)) {
                failedCount++;
            }
        }
        return failedCount == 0;
    }

    public void updateAttachThread(String aid, String tid) {
        attachDAO.updateAttachThread(aid, tid);
    }

    public Integer threadAttachCount(String tid) {
        return attachDAO.threadAttachCount(tid);
    }

    public String uid(String aid) {
        return attachDAO.uid(aid);
    }

    public String insert(AttachUpload attachUpload) {
        attachDAO.insert(attachUpload);
        return attachUpload.getAid();
    }

    public List<Attach> findUnused(String uid) {
        return attachDAO.findUnused(uid);
    }

    private void attachThread(String aid, String tid) {
        attachDAO.updateAttachThread(aid, tid);
    }

    private Boolean attachAccess(String aid, String uid) {
        Attach attach = attachDAO.find(aid);
        return attach.getTid() == null && attach.getUser().getUid().equals(uid);
    }

    public void batchAttachThread(List<String> attachList, String tid, String uid) {
        if (attachList != null && attachList.size() != 0) {
            for (String aid : attachList) {
                if (this.attachAccess(aid, uid)) {
                    this.attachThread(aid, tid);
                }
            }
        }
    }

    public Attach find(String aid) {
        return attachDAO.find(aid);
    }
}
