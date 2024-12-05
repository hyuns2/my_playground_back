package dev.hyun.playground.domain.chatting.service;

import dev.hyun.playground.domain.chatting.dto.ChattingDto;
import dev.hyun.playground.domain.chatting.entity.ChatRoom;
import dev.hyun.playground.domain.chatting.repository.ChatRoomRepository;
import dev.hyun.playground.domain.notification.service.NotificationService;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ChatRoomService {
    private final ChatRoomRepository chatRoomRepository;
    private final NotificationService notificationService;

    public void createChatRoom(ChattingDto.ChatRoomRequest dto) {
        chatRoomRepository.save(ChattingDto.ChatRoomRequest.toEntity(dto));
    }

    public boolean authorizeToAccess(Long chatRoomId, Long userId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND));

        return chatRoom.getUserIdList().contains(userId);
    }

    public void notifyChatMessage(Long chatRoomId, Long senderId) {
        ChatRoom chatRoom = chatRoomRepository.findById(chatRoomId)
                .orElseThrow(() -> new CustomException(CustomErrorCode.CHATROOM_NOT_FOUND));

        chatRoom.getUserIdList().parallelStream()
                .forEach((userId) -> {
                    if (!userId.equals(senderId))
                        notificationService.send(userId, "메시지가 도착했어요!");
                });
    }
}
