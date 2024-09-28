package dev.hyun.playground.domain.account.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "refresh_token")
@Getter
@Builder
public class RefreshToken {
    @Id
    private Long accountId;

    @Indexed
    private String token;

    @TimeToLive
    private Long expTime;
}
