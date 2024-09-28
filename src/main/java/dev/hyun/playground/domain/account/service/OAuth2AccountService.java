package dev.hyun.playground.domain.account.service;

import dev.hyun.playground.domain.account.dto.OAuth2Dto;
import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.SocialType;
import dev.hyun.playground.domain.account.repository.AccountRepository;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class OAuth2AccountService extends DefaultOAuth2UserService {
    private final AccountRepository accountRepository;
    private static final String GOOGLE = "google";
    private static final String NAVER = "naver";

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        Map<String, Object> attributes = super.loadUser(userRequest).getAttributes();
        SocialType socialType = getSocialType(userRequest.getClientRegistration().getRegistrationId());
        String userNameAttributeName = userRequest.getClientRegistration()
                .getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();
        OAuth2Dto.OAuth2UserInfoDto oAuth2UserInfoDto = OAuth2Dto.OAuth2UserInfoDto.of(socialType, attributes);
        Account account = getAccount(oAuth2UserInfoDto);

        return new OAuth2Dto.CustomOAuth2User(
                Collections.singleton(new SimpleGrantedAuthority(account.getRole().name())),
                attributes,
                userNameAttributeName,
                account
        );
    }

    private SocialType getSocialType(String registrationId) {
        if (GOOGLE.equals(registrationId))
            return SocialType.GOOGLE;

        if (NAVER.equals(registrationId))
            return SocialType.NAVER;

        throw new CustomException(CustomErrorCode.UNSUPPORTED_SOCIAL_LOGIN);
    }

    private Account getAccount(OAuth2Dto.OAuth2UserInfoDto dto) {
        Optional<Account> account = accountRepository.findBySocialId(dto.getId());
        return account.isEmpty() ? saveAccount(dto) : account.get();
    }

    private Account saveAccount(OAuth2Dto.OAuth2UserInfoDto dto) {
        return accountRepository.save(dto.toEntity());
    }
}
