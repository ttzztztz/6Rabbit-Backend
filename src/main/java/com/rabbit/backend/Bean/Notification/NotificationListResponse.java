package com.rabbit.backend.Bean.Notification;

import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
public class NotificationListResponse {
    private Map<String, Integer> count;
    private List<Notification> list;
}
