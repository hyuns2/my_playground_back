package dev.hyun.playground.global.security.handler;

import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import dev.hyun.playground.global.security.jwt.JwtUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
@RequiredArgsConstructor
public class LogoutSuccessHandlerImpl implements LogoutSuccessHandler {
    private final JwtUtil jwtUtil;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String token = jwtUtil.extractToken(request);

        if (token != null) {
            jwtUtil.invalidateAccessToken(token);
            authentication = jwtUtil.getAuthentication(token);
            jwtUtil.invalidateRefreshToken(authentication);

            response.setStatus(HttpStatus.OK.value());
        }
    }
}
