package dev.hyun.playground.global.infra.chatAI;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;

public class ChatAIDto {
    @Getter
    @AllArgsConstructor
    @Builder
    public static class Request implements Serializable {
        private String inputs;
    }

    @Getter
    @AllArgsConstructor
    public static class Response implements Serializable {
        @JsonProperty(value = "generated_text")
        private String generatedText;
    }
}
