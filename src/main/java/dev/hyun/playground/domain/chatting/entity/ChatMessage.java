package dev.hyun.playground.domain.chatting.entity;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "chat_message")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatMessage {
    @Id
    private String id;

    @Column(nullable = false)
    private Long chatRoomId;

    @Column(nullable = false)
    private Long senderId;

    @Column(nullable = false)
    private String senderName;

    @Column(nullable = false)
    private String messageType;
    @Column(nullable = false)
    private String content;

    public static ChatMessage of(Long chatRoomId, Long senderId, String senderName, ChattingDto.MessageType messageType, String content) {
        return ChatMessage.builder()
                .chatRoomId(chatRoomId)
                .senderId(senderId)
                .senderName(senderName)
                .messageType(messageType.name())
                .content(content).build();
    }

    public void setContent(String content) {
        this.content = content;
    }
}
