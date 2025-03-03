package dev.hyun.playground.domain.notification.service;

import dev.hyun.playground.domain.notification.repository.SseEmitterRepository;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class NotificationService {
    private final SseEmitterRepository sseEmitterRepository;

    public SseEmitter subscribe(Long userId) {
        SseEmitter sseEmitter = new SseEmitter(Long.MAX_VALUE);

        try {
            sseEmitter.send(SseEmitter.event().name("connect"));
        } catch (IOException e) {
            throw new CustomException(CustomErrorCode.FAILED_SSE_CONNECTION);
        }

        sseEmitterRepository.save(userId, sseEmitter);

        sseEmitter.onCompletion(() -> sseEmitterRepository.deleteByUserId(userId));
        sseEmitter.onTimeout(() -> sseEmitterRepository.deleteByUserId(userId));
        sseEmitter.onError((e) -> sseEmitterRepository.deleteByUserId(userId));

        return sseEmitter;
    }

    public void send(Long userId, String content) {
        SseEmitter sseEmitter = sseEmitterRepository.findByUserId(userId);
        if (sseEmitter == null)
            return;
        try {
            sseEmitter.send(SseEmitter.event()
                    .name("notification")
                    .data(content));
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.FAILED_SSE_TRANSMISSION);
        }
    }
}
