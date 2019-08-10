package com.rabbit.backend.Bean.Notification;

import lombok.Data;

@Data
public class NotificationCountResponse {
    private Integer total;
    private Integer unread;
}
