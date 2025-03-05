package dev.hyun.playground.domain.chatAI;

import dev.hyun.playground.global.infra.chatAI.ChatAIDto;
import dev.hyun.playground.global.infra.chatAI.ChatAIUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

@Service
@RequiredArgsConstructor
public class ChatAIService {
    private final ChatAIUtil chatAIUtil;

    public Flux<String> chat(String message) {
        return chatAIUtil.ask(ChatAIDto.Request.builder()
                    .inputs(message).build());
    }
}
