package dev.hyun.playground.global.infra.chatAI;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;

@Component
@NoArgsConstructor
public class ChatAIUtil {
    @Value("${chat_ai.url}")
    private String url;
    @Value("{chat_ai.key}")
    private String key;
    private WebClient webClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @PostConstruct
    public void init() {
        HttpClient httpClient = HttpClient.create()
                .responseTimeout(Duration.ofSeconds(60));

        webClient = WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .baseUrl(url)
                .defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                .defaultHeader("Authorization", "Bearer " + key).build();
    }

    public Flux<String> ask(ChatAIDto.Request request) {
        String requestValue;
        try {
            requestValue = objectMapper.writeValueAsString(request);
        } catch (JsonProcessingException e) {
            throw new CustomException(CustomErrorCode.JSON_PROCESSING_FAIL);
        }

        return webClient.post()
                .accept(MediaType.TEXT_EVENT_STREAM)
                .bodyValue(requestValue)
                .retrieve()
                .bodyToFlux(ChatAIDto.Response.class)
                .filter(response -> { return response.getGeneratedText() != null && !response.getGeneratedText().isBlank(); })
                .map(ChatAIDto.Response::getGeneratedText);
    }
}
