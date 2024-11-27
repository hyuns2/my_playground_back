package dev.hyun.playground.domain.account.dto;

import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.Role;
import dev.hyun.playground.domain.account.entity.SocialType;
import dev.hyun.playground.domain.user.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

public class AccountDto {

    @Data
    public static class CreateEmailVerificationRequest {
        @NotBlank
        String email;
    }

    @Data
    public static class SignUpRequest {
        @NotBlank
        String email;

        @NotBlank
        String verficiationCode;

        @NotBlank
        String password;

        @NotNull
        Role role;

        @NotBlank
        String name;

        public Account toEntity(String encodedPassword) {
            return Account.builder()
                    .email(email)
                    .password(encodedPassword)
                    .role(role)
                    .isActive(true)
                    .socialType(SocialType.OURS)
                    .user(User.builder()
                            .name(name).build())
                    .build();
        }
    }

    @Data
    public static class SignInRequest {
        @NotBlank
        String email;

        @NotBlank
        String password;
    }

    @Data
    public static class ReissueRequest {
        @NotBlank
        String accessToken;

        @NotBlank
        String refreshToken;
    }
}
