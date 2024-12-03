package dev.hyun.playground.domain.notification.controller;

import dev.hyun.playground.domain.notification.service.NotificationService;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Tag(name = "[3. Notification]")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/notification")
public class NotificationController {
    private final NotificationService notificationService;

    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(@RequestParam Long userId) {
        return notificationService.subscribe(userId);
    }
}
