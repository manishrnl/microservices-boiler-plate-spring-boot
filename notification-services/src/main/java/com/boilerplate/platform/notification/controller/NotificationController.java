package com.boilerplate.platform.notification.controller;

import com.boilerplate.platform.notification.dto.NotificationResponse;
import com.boilerplate.platform.notification.service.NotificationService;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "notification-services", "status", "UP");
    }

    @GetMapping
    public List<NotificationResponse> listNotifications() {
        return notificationService.listNotifications();
    }
}
