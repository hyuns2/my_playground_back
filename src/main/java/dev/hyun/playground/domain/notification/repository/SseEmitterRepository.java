package dev.hyun.playground.domain.notification.repository;

import org.springframework.stereotype.Repository;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class SseEmitterRepository {
    public static Map<Long, SseEmitter> sseEmitterMap = new ConcurrentHashMap<>();

    public void save(Long userId, SseEmitter sseEmitter) {
        sseEmitterMap.put(userId, sseEmitter);
    }

    public SseEmitter findByUserId(Long userId) {
        if (sseEmitterMap.containsKey(userId))
            return sseEmitterMap.get(userId);
        return null;
    }

    public void deleteByUserId(Long userId) {
        sseEmitterMap.remove(userId);
    }
}
