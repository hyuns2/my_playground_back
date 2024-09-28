package dev.hyun.playground.domain.account.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@RedisHash
@Getter
@Builder
public class BlackList {
    @Id
    private String accessToken;
}
