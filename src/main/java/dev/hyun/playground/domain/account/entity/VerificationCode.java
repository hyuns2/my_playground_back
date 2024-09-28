package dev.hyun.playground.domain.account.entity;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.TimeToLive;
import org.springframework.data.redis.core.index.Indexed;

@RedisHash(value = "verification_code")
@Getter
@Builder
public class VerificationCode {
    @Id
    private String email;

    @Indexed
    private String code;

    @TimeToLive
    private Long expTime;
}
