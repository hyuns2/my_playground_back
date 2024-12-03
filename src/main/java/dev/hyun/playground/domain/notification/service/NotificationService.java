package dev.hyun.playground.domain.notification.service;

import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class NotificationService {
    public static Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public SseEmitter subscribe(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            throw new IllegalStateException("알림 연결 실패");
        }

        sseEmitterMap.put(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterMap.remove(userId));
        sseEmitter.onTimeout(() -> sseEmitterMap.remove(userId));
        sseEmitter.onError((e) -> sseEmitterMap.remove(userId));

        return sseEmitter;
    }

    public void send(Long userId, String content) {
        if (sseEmitterMap.containsKey(userId)) {
            SseEmitter sseEmitter = sseEmitterMap.get(userId);
            try {
                sseEmitter.send(SseEmitter.event()
                        .name("notification")
                        .data(content));
            } catch (Exception e) {
                throw new IllegalStateException("알림 전송 실패");
            }
        }
    }
}
