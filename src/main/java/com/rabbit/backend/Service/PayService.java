package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Attach.Attach;
import com.rabbit.backend.Bean.Attach.AttachListItem;
import com.rabbit.backend.Bean.Attach.ThreadAttach;
import com.rabbit.backend.Bean.Credits.AttachPayLog;
import com.rabbit.backend.Bean.Credits.CreditsPay;
import com.rabbit.backend.Bean.Credits.ThreadPayLog;
import com.rabbit.backend.Bean.Thread.ThreadItem;
import com.rabbit.backend.Bean.Thread.ThreadListItem;
import com.rabbit.backend.Bean.User.MyUser;
import com.rabbit.backend.DAO.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.function.Supplier;

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

    private Boolean needPayDecide(Integer creditsType, Integer credits, String currentUid, String authorUid,
                                  Supplier<Boolean> predicate) {
        if (currentUid == null) {
            return creditsType != 0 && credits != 0;
        }

        if (creditsType == 0 || credits == 0 || currentUid.equals(authorUid)) {
            return false;
        } else {
            return predicate.get();
        }
    }

    public Boolean userThreadNeedPay(String uid, ThreadListItem threadListItem) {
        return needPayDecide(threadListItem.getCreditsType(), threadListItem.getCredits(), uid,
                threadListItem.getUser().getUid(), () -> {
                    Integer isPayResult = threadPayDAO.isPay(threadListItem.getTid(), uid);
                    return isPayResult == null || isPayResult != 1;
                });
    }

    public Boolean userThreadNeedPay(String uid, ThreadItem threadItem) {
        return needPayDecide(threadItem.getCreditsType(), threadItem.getCredits(), uid,
                threadItem.getUser().getUid(), () -> {
                    Integer isPayResult = threadPayDAO.isPay(threadItem.getTid(), uid);
                    return isPayResult == null || isPayResult != 1;
                });
    }

    public Boolean userAttachNeedPay(String uid, String aid) {
        Attach attach = attachDAO.find(aid);

        return needPayDecide(attach.getCreditsType(), attach.getCredits(), uid, attach.getUser().getUid(),
                () -> {
                    Integer isPayResult = attachPayDAO.isPay(aid, uid);
                    return isPayResult == null || isPayResult != 1;
                });
    }

    public Boolean userAttachDownloadAccess(String uid, Attach attach, ThreadListItem threadListItem) {
        String tid = threadListItem.getTid();

        return (!userAttachNeedPay(uid, attach.getAid())) && (!userThreadNeedPay(uid, threadListItem));
    }

    public List<ThreadListItem> threadPurchasedList(String uid, Integer page) {
        return threadPayDAO.findByUid(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public List<AttachListItem> attachPurchasedList(String uid, Integer page) {
        return attachPayDAO.findByUid(uid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    private void insertPurchaseThread(String uid, String tid, Integer creditsType, Integer credits) {
        ThreadPayLog threadPayLog = new ThreadPayLog();
        threadPayLog.setTid(tid);
        threadPayLog.setUid(uid);
        threadPayLog.setCreditsType(creditsType);
        threadPayLog.setCredits(credits);
        threadPayDAO.insert(threadPayLog);
    }

    private void insertPurchaseAttach(String uid, String aid, Integer creditsType, Integer credits) {
        AttachPayLog attachPayLog = new AttachPayLog();
        attachPayLog.setAid(aid);
        attachPayLog.setUid(uid);
        attachPayLog.setCreditsType(creditsType);
        attachPayLog.setCredits(credits);
        attachPayDAO.insert(attachPayLog);
    }

    private Boolean decreasePurchaseCredits(String buyerUid, String sellerUid, CreditsPay creditsPay) {
        MyUser user = userDAO.findMy("uid", buyerUid);
        switch (creditsPay.getCreditsType()) {
            case 0:
                return true;
            case 1:
                if (user.getCredits() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(buyerUid, "credits", creditsPay.getCredits());
                    userDAO.increaseCredits(sellerUid, "credits", creditsPay.getCredits());
                    return true;
                }
            case 2:
                if (user.getGolds() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(buyerUid, "golds", creditsPay.getCredits());
                    userDAO.increaseCredits(sellerUid, "golds", creditsPay.getCredits());
                    return true;
                }
            case 3:
                if (user.getRmbs() < creditsPay.getCredits()) {
                    return false;
                } else {
                    userDAO.decreaseCredits(buyerUid, "rmbs", creditsPay.getCredits());
                    userDAO.increaseCredits(sellerUid, "rmbs", creditsPay.getCredits());
                    return true;
                }
            default:
                return false;
        }
    }

    @Transactional
    public Boolean purchaseThread(String buyerUid, String sellerUid, ThreadListItem threadListItem) {
        String tid = threadListItem.getTid();
        CreditsPay creditsPay = threadPayDAO.creditsPay(tid);

        if (!decreasePurchaseCredits(buyerUid, sellerUid, creditsPay)) {
            return false;
        } else {
            insertPurchaseThread(buyerUid, tid, threadListItem.getCreditsType(), threadListItem.getCredits());
            return true;
        }
    }

    @Transactional
    public Boolean purchaseAttach(String buyerUid, String sellerUid, String aid) {
        CreditsPay creditsPay = attachPayDAO.creditsPay(aid);
        ThreadAttach attach = attachDAO.findWithThreadAttach(aid);

        if (!decreasePurchaseCredits(buyerUid, sellerUid, creditsPay)) {
            return false;
        } else {
            insertPurchaseAttach(buyerUid, aid, attach.getCreditsType(), attach.getCredits());
            return true;
        }
    }
}
