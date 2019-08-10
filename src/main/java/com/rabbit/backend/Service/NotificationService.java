package com.rabbit.backend.Service;

import com.rabbit.backend.Bean.Notification.Notification;
import com.rabbit.backend.Bean.Notification.NotificationCountResponse;
import com.rabbit.backend.DAO.NotificationDAO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class NotificationService {
    private NotificationDAO DAO;

    @Value("${rabbit.pagesize}")
    private Integer PAGESIZE;

    @Autowired
    public NotificationService(NotificationDAO notificationDAO) {
        this.DAO = notificationDAO;
    }

    public List<Notification> list(String toUid, Integer page) {
        return DAO.list(toUid, (page - 1) * PAGESIZE, page * PAGESIZE);
    }

    public Boolean setOneRead(String nid, String toUid) {
        String realToUid = DAO.toUid(nid);
        if (realToUid != null && realToUid.equals(toUid)) {
            DAO.setOneRead(nid);
            return true;
        } else {
            return false;
        }
    }

    public void setAllRead(String toUid) {
        DAO.setAllRead(toUid);
    }

    public Boolean deleteOne(String nid, String toUid) {
        String realToUid = DAO.toUid(nid);

        if (realToUid != null && realToUid.equals(toUid)) {
            DAO.deleteOne(nid);
            return true;
        } else {
            return false;
        }
    }

    public void deleteAll(String toUid) {
        DAO.deleteAll(toUid);
    }

    public NotificationCountResponse count(String toUid) {
        NotificationCountResponse result = new NotificationCountResponse();

        result.setTotal(DAO.totalCount(toUid));
        result.setUnread(DAO.unreadCount(toUid));

        return result;
    }

    @Async
    public void push(String fromUid, String toUid, String content, String link) {
        DAO.insert(fromUid, toUid, content, link);
    }
}
