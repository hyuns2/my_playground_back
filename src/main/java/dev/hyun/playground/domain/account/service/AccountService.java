package dev.hyun.playground.domain.account.service;

import dev.hyun.playground.domain.account.dto.AccountDto;
import dev.hyun.playground.domain.account.entity.Account;
import dev.hyun.playground.domain.account.entity.VerificationCode;
import dev.hyun.playground.domain.account.repository.AccountRepository;
import dev.hyun.playground.domain.account.repository.VerificationCodeRepository;
import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import dev.hyun.playground.global.security.jwt.JwtGenerator;
import dev.hyun.playground.global.security.jwt.JwtToken;
import dev.hyun.playground.global.security.jwt.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;

@Service
@RequiredArgsConstructor
public class AccountService implements UserDetailsService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtGenerator jwtGenerator;
    private final JwtUtil jwtUtil;
    private final EmailService emailService;
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${spring.mail.code_exp_time}")
    private Long codeExpTime;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return accountRepository.findByEmail(username)
                .orElseThrow(() -> new CustomException(CustomErrorCode.ACCOUNT_NOT_FOUND));
    }

    @Transactional
    public void createEmailVerification(AccountDto.CreateEmailVerificationRequest dto) {
        checkExistedAccount(dto.getEmail());

        String code = generateVerificationCode();
        emailService.sendEmail(dto.getEmail(), "Playground Verification Code", code);

        verificationCodeRepository.save(VerificationCode.builder()
                .email(dto.getEmail())
                .code(code)
                .expTime(codeExpTime).build());
    }

    private void checkExistedAccount(String email) {
        if (accountRepository.findByEmail(email).isPresent())
            throw new CustomException(CustomErrorCode.EXISTED_ACCOUNT);
    }

    private String generateVerificationCode() {
        int length = 6;
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

        SecureRandom secureRandom = new SecureRandom();
        StringBuilder stringBuilder = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            int index = secureRandom.nextInt(characters.length());
            stringBuilder.append(characters.charAt(index));
        }
        return stringBuilder.toString();
    }

    @Transactional
    public void signUp(AccountDto.SignUpRequest dto) {
        VerificationCode savedCode = verificationCodeRepository.findById(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.INVALID_VERIFICATION_CODE));
        if (!dto.getVerficiationCode().equals(savedCode.getCode()))
            throw new CustomException(CustomErrorCode.INVALID_VERIFICATION_CODE);
        verificationCodeRepository.deleteById(dto.getEmail());

        Account account = dto.toEntity(passwordEncoder.encode(dto.getPassword()));
        accountRepository.save(account);
    }

    @Transactional
    public JwtToken signIn(AccountDto.SignInRequest dto) {
        Account account = accountRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new CustomException(CustomErrorCode.ACCOUNT_NOT_FOUND));

        if (!passwordEncoder.matches(dto.getPassword(), account.getPassword()))
            throw new CustomException(CustomErrorCode.ACCOUNT_NOT_FOUND);

        return jwtGenerator.generateToken(new UsernamePasswordAuthenticationToken(
                account, null, account.getAuthorities()));
    }

    @Transactional
    public JwtToken reissueToken(AccountDto.ReissueRequest dto) {
        jwtUtil.validateToken(dto.getRefreshToken());

        return jwtUtil.reissueToken(dto.getAccessToken(), dto.getRefreshToken());
    }
}
