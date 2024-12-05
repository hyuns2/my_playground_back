package dev.hyun.playground.domain.chatting.controller;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import dev.hyun.playground.domain.chatting.service.ChatMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ChatMessageController {
    private final ChatMessageService chatMessageService;

    @MessageMapping("/chat-message")
    public void sendMessage(ChattingDto.ChatMessageRequest chatMessageRequest) {
        chatMessageService.sendMessage(chatMessageRequest);
    }

    @KafkaListener(topics = "chatting")
    public void receiveMessage(ChattingDto.ChatMessageRequest chatMessageRequest) {
        chatMessageService.receiveMessage(chatMessageRequest);
    }

//    {"chatRoomId": 1, "senderId": 1, "senderName": "창1", "messageType": "TALK", "content": "hello"}
}
