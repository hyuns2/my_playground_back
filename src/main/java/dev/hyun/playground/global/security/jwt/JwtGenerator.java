package dev.hyun.playground.global.security.jwt;

import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.RefreshToken;
import dev.hyun.playground.domain.account.repository.RefreshTokenRepository;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.stream.Collectors;

@Component
@Getter
public class JwtGenerator {
    private final Key key;
    private final Long accessExpTime;
    private final Long refreshExpTime;
    private final RefreshTokenRepository refreshTokenRepository;

    public JwtGenerator(@Value("${jwt.secret_key}") String secretKey,
                        @Value("${jwt.access_exp_time}") Long accessExpTime,
                        @Value("${jwt.refresh_exp_time}") Long refreshExpTime,
                        RefreshTokenRepository refreshTokenRepository) {
        this.key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretKey));
        this.accessExpTime = accessExpTime;
        this.refreshExpTime = refreshExpTime;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    public JwtToken generateToken(Authentication authentication) {
        Account account = (Account) authentication.getPrincipal();
        String authorities = authentication.getAuthorities()
                .stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(","));

        String accessToken = Jwts.builder()
                .claim("username", account.getEmail())
                .claim("auth", authorities)
                .setExpiration(new Date(System.currentTimeMillis()+ accessExpTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        String refreshToken = Jwts.builder()
                .setExpiration(new Date(System.currentTimeMillis()+ refreshExpTime))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
        saveRefreshToken(account.getId(), refreshToken);

        return JwtToken.builder()
                .grantType("Bearer")
                .accessToken(accessToken)
                .refreshToken(refreshToken).build();
    }

    private void saveRefreshToken(Long accountId, String token) {
        refreshTokenRepository.save(RefreshToken.builder()
                .accountId(accountId)
                .token(token)
                .expTime(this.refreshExpTime).build());
    }
}
