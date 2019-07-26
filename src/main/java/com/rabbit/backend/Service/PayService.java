package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachPayListItem;
import com.rabbit.backend.Bean.Credits.AttachPayLog;
import com.rabbit.backend.Bean.Credits.CreditsPay;
import com.rabbit.backend.Bean.Credits.ThreadPayLog;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import com.rabbit.backend.Bean.User.MyUser;
import com.rabbit.backend.DAO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class PayService {
    private UserDAO userDAO;
    private ThreadPayDAO threadPayDAO;
    private ThreadDAO threadDAO;
    private AttachPayDAO attachPayDAO;
    private AttachDAO attachDAO;

    @Autowired
    public PayService(UserDAO userDAO, ThreadPayDAO threadPayDAO, ThreadDAO threadDAO,
                      AttachPayDAO attachPayDAO, AttachDAO attachDAO) {
        this.userDAO = userDAO;
        this.threadPayDAO = threadPayDAO;
        this.threadDAO = threadDAO;
        this.attachPayDAO = attachPayDAO;
        this.attachDAO = attachDAO;
    }

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    public Boolean userThreadNeedPay(String uid, String tid) {
        ThreadListItem threadListItem = threadDAO.findThreadListItem(tid);
        if (threadListItem.getCreditsType() == 0 || threadListItem.getCredits() == 0
                || threadListItem.getUser().getUid().equals(uid)) {
            return false;
        } else {
            return threadPayDAO.isPay(tid, uid) != 1;
        }
    }

    public Boolean userAttachNeedPay(String uid, String aid) {
        Attach attach = attachDAO.find(aid);
        if (attach.getCreditsType() == 0 || attach.getCredits() == 0
                || attach.getUser().getUid().equals(uid)) {
            return false;
        } else {
            return attachPayDAO.isPay(aid, uid) != 1;
        }
    }

    public Boolean userAttachDownloadAccess(String uid, Attach attach, String tid) {
        CreditsPay threadCreditsPay = threadPayDAO.creditsPay(tid);

        if (threadCreditsPay.getCreditsType() != 0 && threadCreditsPay.getCredits() != 0
                && userThreadNeedPay(uid, tid)
        ) {
            return false;
        }

        if (attach.getCreditsType() == 0 || attach.getCredits() == 0
                || attach.getUser().getUid().equals(uid)) {
            return true;
        } else {
            return attachPayDAO.isPay(attach.getAid(), uid) == 1;
        }
    }

    public List<ThreadListItem> threadPurchasedList(String uid, Integer page) {
        return threadPayDAO.findByUid(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public List<AttachPayListItem> attachPurchasedList(String uid, Integer page) {
        return attachPayDAO.findByUid(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    private void insertPurchaseThread(String uid, String tid) {
        ThreadPayLog threadPayLog = new ThreadPayLog();
        threadPayLog.setTid(tid);
        threadPayLog.setUid(uid);
        threadPayDAO.insert(threadPayLog);
    }

    private void insertPurchaseAttach(String uid, String aid) {
        AttachPayLog attachPayLog = new AttachPayLog();
        attachPayLog.setAid(aid);
        attachPayLog.setUid(uid);
        attachPayDAO.insert(attachPayLog);
    }

    private Boolean decreasePurchaseCredits(String uid, CreditsPay creditsPay) {
        MyUser user = userDAO.findMy("uid", uid);
        switch (creditsPay.getCreditsType()) {
            case 0:
                return true;
            case 1:
                if (user.getCredits() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(uid, "credits", creditsPay.getCredits().toString());
                    return true;
                }
            case 2:
                if (user.getGolds() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(uid, "golds", creditsPay.getCredits().toString());
                    return true;
                }
            case 3:
                if (user.getRmbs() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(uid, "rmbs", creditsPay.getCredits().toString());
                    return true;
                }
            default:
                return false;
        }
    }

    @Transactional
    public Boolean purchaseThread(String uid, String tid) {
        CreditsPay creditsPay = threadPayDAO.creditsPay(tid);
        if (!decreasePurchaseCredits(uid, creditsPay)) {
            return false;
        } else {
            insertPurchaseThread(uid, tid);
            return true;
        }
    }

    @Transactional
    public Boolean purchaseAttach(String uid, String aid) {
        CreditsPay creditsPay = attachPayDAO.creditsPay(aid);
        if (!decreasePurchaseCredits(uid, creditsPay)) {
            return false;
        } else {
            insertPurchaseAttach(uid, aid);
            return true;
        }
    }
}
