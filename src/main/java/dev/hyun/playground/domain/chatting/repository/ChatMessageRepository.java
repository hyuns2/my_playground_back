package dev.hyun.playground.domain.chatting.repository;

import dev.hyun.playground.domain.chatting.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
