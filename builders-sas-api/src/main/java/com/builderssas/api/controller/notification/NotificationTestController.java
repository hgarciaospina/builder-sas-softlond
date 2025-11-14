package com.builderssas.api.controller.notification;

import com.builderssas.api.domain.model.notification.NotificationDto;
import com.builderssas.api.services.notification.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationTestController {

    private final NotificationService notificationService;

    @PostMapping("/test")
    public String sendTest() {
        notificationService.send(
                NotificationDto.builder()
                        .eventType("TEST_EVENT")
                        .payload("Probando webhook desde Builders-SAS")
                        .build()
        );
        return "✅ Notificación enviada (si todo está bien)";
    }
}