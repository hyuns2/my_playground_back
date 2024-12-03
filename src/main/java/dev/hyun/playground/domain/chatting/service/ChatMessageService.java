package dev.hyun.playground.domain.chatting.service;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import dev.hyun.playground.domain.chatting.entity.ChatMessage;
import dev.hyun.playground.domain.chatting.repository.ChatMessageRepository;
import dev.hyun.playground.domain.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatMessageRepository chatMessageRepository;
    private final KafkaTemplate<String, ChattingDto.ChatRequest> kafkaTemplate;
    private final NotificationService notificationService;
    private static final String KAFKA_TOPIC = "chatting";

    public void sendMessage(ChattingDto.ChatRequest chatRequest) {
        kafkaTemplate.send(KAFKA_TOPIC, chatRequest);
    }

    @Transactional
    public void receiveMessage(ChattingDto.ChatRequest chatRequest) {
        ChatMessage chatMessage = toChatMessage(chatRequest);
        messageSendingOperations.convertAndSend("/sub/chat-room/" + chatRequest.getChatRoomId(), chatMessage);
        chatMessageRepository.save(chatMessage);
    }

    private ChatMessage toChatMessage(ChattingDto.ChatRequest chatRequest) {
        ChatMessage chatMessage = ChatMessage.of(chatRequest.getChatRoomId(), chatRequest.getSenderId(), chatRequest.getSenderName(), chatRequest.getMessageType(), chatRequest.getContent());
        if (chatMessage.getMessageType().equals(ChattingDto.MessageType.ENTER.name()))
            chatMessage.setContent(chatMessage.getSenderName() + "님이 입장하셨습니다!");
        else if (chatMessage.getMessageType().equals(ChattingDto.MessageType.EXIT.name()))
            chatMessage.setContent(chatMessage.getSenderName() + "님이 퇴장하셨습니다!");

        return chatMessage;
    }
}
