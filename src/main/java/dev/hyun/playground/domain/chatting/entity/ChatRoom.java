package dev.hyun.playground.domain.chatting.entity;

import dev.hyun.playground.global.converter.LongListToStringConverter;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class ChatRoom {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @Convert(converter = LongListToStringConverter.class)
    private List<Long> userIdList = new ArrayList<>();
}
