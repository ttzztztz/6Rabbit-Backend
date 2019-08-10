package com.rabbit.backend.Bean.Notification;

import lombok.Data;

import java.util.List;

@Data
public class NotificationListResponse {
    private NotificationCountResponse count;
    private List<Notification> list;
}
