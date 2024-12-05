package dev.hyun.playground.domain.chatting.dto;

import dev.hyun.playground.domain.chatting.entity.ChatRoom;
import dev.hyun.playground.domain.chatting.mongo.entity.ChatMessage;
import lombok.*;

import java.io.Serializable;
import java.util.List;

public class ChattingDto {

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatRoomRequest {
        private List<Long> userIdList;

        public static ChatRoom toEntity(ChatRoomRequest dto) {
            return ChatRoom.builder()
                    .userIdList(dto.getUserIdList()).build();
        }
    }

    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ChatMessageRequest implements Serializable {
        private Long chatRoomId;
        private Long senderId;
        private String senderName;
        private MessageType messageType;
        private String content;

        public enum MessageType {
            ENTER, TALK, EXIT
        }

        public static ChatMessage toEntity(ChatMessageRequest dto) {
            return ChatMessage.builder()
                    .chatRoomId(dto.getChatRoomId())
                    .senderId(dto.getSenderId())
                    .senderName(dto.getSenderName())
                    .messageType(dto.getMessageType().name())
                    .content(dto.getContent()).build();
        }
    }
}
