package dev.hyun.playground.domain.account.controller;

import dev.hyun.playground.domain.account.dto.AccountDto;
import dev.hyun.playground.domain.account.service.AccountService;
import dev.hyun.playground.global.security.jwt.JwtToken;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "[1. Account]")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/account")
public class AccountController {
    private final AccountService accountService;

    @Operation(summary = "이메일 인증코드 생성")
    @PostMapping("/email-verification")
    public ResponseEntity<Void> createEmailVerification(@Valid @RequestBody AccountDto.CreateEmailVerificationRequest dto) {
        accountService.createEmailVerification(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "회원가입")
    @PostMapping("/sign-up")
    public ResponseEntity<Void> signUp(@Valid @RequestBody AccountDto.SignUpRequest dto) {
        accountService.signUp(dto);

        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @Operation(summary = "로그인")
    @PostMapping("/sign-in")
    public ResponseEntity<JwtToken> signIn(@Valid @RequestBody AccountDto.SignInRequest dto) {
        JwtToken token = accountService.signIn(dto);

        return new ResponseEntity<>(token, HttpStatus.OK);
    }

    @Operation(summary = "리프레시 토큰 재발급")
    @PostMapping("/reissue-token")
    public ResponseEntity<JwtToken> reissueRefreshToken(@Valid @RequestBody AccountDto.ReissueRequest dto) {
        JwtToken token = accountService.reissueToken(dto);

        return new ResponseEntity<>(token, HttpStatus.CREATED);
    }

    @Operation(summary = "로그아웃")
    @PostMapping("/sign-out")
    public ResponseEntity<Void> signOut() {
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
