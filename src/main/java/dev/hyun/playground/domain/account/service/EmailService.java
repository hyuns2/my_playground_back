package dev.hyun.playground.domain.account.service;

import dev.hyun.playground.global.error.CustomErrorCode;
import dev.hyun.playground.global.error.CustomException;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender javaMailSender;

    @Transactional
    public void sendEmail(String toEmail, String title, String content) {
        SimpleMailMessage emailForm = createEmailForm(toEmail, title, content);
        try {
            javaMailSender.send(emailForm);
        } catch (RuntimeException e) {
            throw new CustomException(CustomErrorCode.EMAIL_SEND_FAIL);
        }
    }

    private SimpleMailMessage createEmailForm(String toEmail, String title, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(title);
        message.setText(content);

        return message;
    }
}
