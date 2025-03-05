package dev.hyun.playground.domain.chatting.service;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import dev.hyun.playground.domain.chatting.mongo.entity.ChatMessage;
import dev.hyun.playground.domain.chatting.mongo.repository.ChatMessageRepository;
import dev.hyun.playground.domain.user.User;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.simp.SimpMessageSendingOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatMessageService {
    private final SimpMessageSendingOperations messageSendingOperations;
    private final ChatMessageRepository chatMessageRepository;
    private final KafkaTemplate<String, ChattingDto.ChatMessageRequest> kafkaTemplate;
    private final ChatRoomService chatRoomService;
    private static final String KAFKA_TOPIC = "chatting";

    public void sendMessage(User user, ChattingDto.ChatMessageRequest dto) {
        if (!user.getId().equals(dto.getSenderId()))
            throw new CustomException(CustomErrorCode.SENDER_NOT_MATCHED);

        if (chatRoomService.authorizeToAccess(dto.getChatRoomId(), dto.getSenderId()))
            kafkaTemplate.send(KAFKA_TOPIC, dto);
        else
            throw new CustomException(CustomErrorCode.FORBIDDEN_CHATROOM);
    }

    public void receiveMessage(ChattingDto.ChatMessageRequest dto) {
        ChatMessage chatMessage = toChatMessage(dto);
        messageSendingOperations.convertAndSend("/sub/chat-room/" + dto.getChatRoomId(), chatMessage);
        chatRoomService.notifyChatMessage(dto.getChatRoomId(), dto.getSenderId());
        chatMessageRepository.save(chatMessage);
    }

    private ChatMessage toChatMessage(ChattingDto.ChatMessageRequest dto) {
        ChatMessage chatMessage = ChattingDto.ChatMessageRequest.toEntity(dto);
        if (chatMessage.getMessageType().equals(ChattingDto.ChatMessageRequest.MessageType.ENTER.name()))
            chatMessage.setContent(chatMessage.getSenderName() + "님이 입장하셨습니다!");
        else if (chatMessage.getMessageType().equals(ChattingDto.ChatMessageRequest.MessageType.EXIT.name()))
            chatMessage.setContent(chatMessage.getSenderName() + "님이 퇴장하셨습니다!");

        return chatMessage;
    }
}
