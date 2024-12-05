package dev.hyun.playground.domain.chatting.mongo.repository;

import dev.hyun.playground.domain.chatting.mongo.entity.ChatMessage;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ChatMessageRepository extends MongoRepository<ChatMessage, String> {
}
