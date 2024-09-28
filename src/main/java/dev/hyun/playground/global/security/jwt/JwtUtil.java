package dev.hyun.playground.global.security.jwt;

import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.BlackList;
import dev.hyun.playground.domain.account.entity.RefreshToken;
import dev.hyun.playground.domain.account.repository.AccountRepository;
import dev.hyun.playground.domain.account.repository.BlackListRepository;
import dev.hyun.playground.domain.account.repository.RefreshTokenRepository;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import io.jsonwebtoken.*;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.Arrays;
import java.util.Collection;

@Component
@RequiredArgsConstructor
public class JwtUtil {
    private final JwtGenerator jwtGenerator;
    private final AccountRepository accountRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final BlackListRepository blackListRepository;

    public Authentication getAuthentication(String token) {
        Claims claims = parseClaims(token);

        if (claims.get("auth") == null) {
            throw new AccessDeniedException(null);
        }

        Collection<? extends GrantedAuthority> authorities = Arrays.stream(
                claims.get("auth").toString().split(","))
                        .map(SimpleGrantedAuthority::new).toList();

        UserDetails account = accountRepository.findByEmail((String) claims.get("username"))
                .orElseThrow(() -> new CustomException(CustomErrorCode.ACCOUNT_NOT_FOUND));
        return new UsernamePasswordAuthenticationToken(account, null, authorities);
    }

    private Claims parseClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(jwtGenerator.getKey()).build()
                    .parseClaimsJws(token).getBody();
        } catch (ExpiredJwtException e) {
            return e.getClaims();
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
    }

    public String extractToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer"))
            return bearerToken.substring(7);
        return null;
    }

    public boolean validateToken(String token) {
        if (blackListRepository.existsById(token))
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);

        try {
            Jwts.parserBuilder().setSigningKey(jwtGenerator.getKey()).build()
                    .parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
        }
    }

    public void invalidateAccessToken(String accessToken) {
        blackListRepository.save(BlackList.builder()
                .accessToken(accessToken).build());
    }

    public void invalidateRefreshToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        if (refreshTokenRepository.findById(account.getId()).isPresent())
            refreshTokenRepository.deleteById(account.getId());
        else
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);
    }

    public JwtToken reissueToken(String accessToken, String refreshToken) {
        Authentication authentication = getAuthentication(accessToken);
        Account account = (Account) authentication.getPrincipal();

        RefreshToken savedRefreshToken = refreshTokenRepository.findById(account.getId())
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_TOKEN));

        if (!refreshToken.equals(savedRefreshToken.getToken()))
            throw new CustomException(CustomErrorCode.INVALID_TOKEN);

        refreshTokenRepository.deleteById(account.getId());
        blackListRepository.save(BlackList.builder()
                .accessToken(accessToken).build());
        return jwtGenerator.generateToken(
                new UsernamePasswordAuthenticationToken(account, null, account.getAuthorities()));
    }
}
