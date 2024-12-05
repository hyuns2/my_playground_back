package dev.hyun.playground.domain.chatting.mongo.entity;

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

    private Long chatRoomId;

    private Long senderId;

    private String senderName;

    private String messageType;
    private String content;

    public void setContent(String content) {
        this.content = content;
    }
}
