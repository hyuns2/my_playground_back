package dev.hyun.playground.global.security.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import dev.hyun.playground.domain.account.dto.OAuth2Dto;
import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.global.security.jwt.JwtGenerator;
import dev.hyun.playground.global.security.jwt.JwtToken;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler implements AuthenticationSuccessHandler {
    private final JwtGenerator jwtGenerator;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        Account account = ((OAuth2Dto.CustomOAuth2User) authentication.getPrincipal()).getAccount();
        Authentication authenticationToken = new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities());

        JwtToken jwtToken = jwtGenerator.generateToken(authenticationToken);
        setResponse(response, jwtToken);
    }

    private void setResponse(HttpServletResponse response, JwtToken jwtToken) throws IOException {
        response.setStatus(HttpStatus.OK.value());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setCharacterEncoding("UTF-8");

        ObjectMapper objectMapper = new ObjectMapper();
        response.getWriter().write(objectMapper.writeValueAsString(jwtToken));
    }
}
