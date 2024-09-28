package dev.hyun.playground.domain.account.dto;

import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.Role;
import dev.hyun.playground.domain.account.entity.SocialType;
import dev.hyun.playground.domain.user.User;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;

import java.util.Collection;
import java.util.Map;

public class OAuth2Dto {

    @Getter
    @AllArgsConstructor
    @Builder
    public static class OAuth2UserInfoDto {
        private String id;
        private SocialType socialType;
        private String email;
        private String name;

        public static OAuth2UserInfoDto of(SocialType socialType, Map<String, Object> attributes) {
            if (socialType.equals(SocialType.GOOGLE))
                return ofGoogle(attributes);

            if (socialType.equals(SocialType.NAVER))
                return ofNaver(attributes);

            throw new CustomException(CustomErrorCode.UNSUPPORTED_SOCIAL_LOGIN);
        }

        private static OAuth2UserInfoDto ofGoogle(Map<String, Object> attributes) {
            return OAuth2UserInfoDto.builder()
                    .id((String) attributes.get("sub"))
                    .socialType(SocialType.GOOGLE)
                    .email((String) attributes.get("email"))
                    .name((String) attributes.get("name")).build();
        }

        private static OAuth2UserInfoDto ofNaver(Map<String, Object> attributes) {
            Map<String, Object> response = (Map<String, Object>) attributes.get("response");

            return OAuth2UserInfoDto.builder()
                    .id((String) response.get("id"))
                    .socialType(SocialType.NAVER)
                    .email((String) response.get("email"))
                    .name((String) response.get("name")).build();
        }

        public Account toEntity() {
            User user = User.builder()
                    .name(name).build();

            return Account.builder()
                    .email(email)
                    .password(null)
                    .role(Role.ROLE_USER)
                    .isActive(true)
                    .socialType(socialType)
                    .socialId(socialType.name() + "_" + id)
                    .user(user).build();
        }
    }

    @Getter
    public static class CustomOAuth2User extends DefaultOAuth2User {
        private Account account;

        public CustomOAuth2User(Collection<? extends GrantedAuthority> authorities,
                                Map<String, Object> attributes,
                                String nameAttributeKey,
                                Account account) {
            super(authorities, attributes, nameAttributeKey);
            this.account = account;
        }
    }
}

