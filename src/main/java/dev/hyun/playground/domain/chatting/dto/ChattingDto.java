package dev.hyun.playground.domain.chatting.dto;

import lombok.*;

import java.io.Serializable;

public class ChattingDto {
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRequest implements Serializable {
        private Long chatRoomId;
        private Long senderId;
        private String senderName;
        private MessageType messageType;
        private String content;
    }

    public enum MessageType {
        ENTER, TALK, EXIT
    }
}
