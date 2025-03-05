package dev.hyun.playground.global.error;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum CustomErrorCode {
    // origin
    UNAUTHORIZED("Origin-401", "인증되지 않은 요청입니다.", HttpStatus.UNAUTHORIZED),
    FORBIDDEN("Origin-403", "접근 권한이 없습니다.", HttpStatus.FORBIDDEN),
    SERVER_ERROR("Origin-500", "서버에 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR),

    // account
    ACCOUNT_NOT_FOUND("Account-001", "해당하는 계정을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    INVALID_TOKEN("Account-002", "유효하지 않은 토큰입니다.", HttpStatus.BAD_REQUEST),
    EMAIL_SEND_FAIL("Account-003", "이메일 발송을 실패했습니다.", HttpStatus.BAD_REQUEST),
    EXISTED_ACCOUNT("Account-004", "이메일로 가입된 계정이 이미 존재합니다.", HttpStatus.BAD_REQUEST),
    INVALID_VERIFICATION_CODE("Account-005", "이메일 인증을 실패했습니다.", HttpStatus.BAD_REQUEST),
    SOCIAL_LOGIN_FAIL("Account-006", "소셜 로그인을 실패했습니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_SOCIAL_LOGIN("Account-007", "지원하지 않는 소셜 로그인입니다.", HttpStatus.BAD_REQUEST),

    // chatting
    CHATROOM_NOT_FOUND("Chatting-001", "해당하는 채팅방을 찾을 수 없습니다.", HttpStatus.NOT_FOUND),
    SENDER_NOT_MATCHED("Chatting-002", "현재 유저와 채팅 발신자가 일치하지 않습니다.", HttpStatus.BAD_REQUEST),
    FORBIDDEN_CHATROOM("Chatting-003", "해당 채팅방의 접근 권한이 없습니다.", HttpStatus.FORBIDDEN),

    // notification
    FAILED_SSE_CONNECTION("Notification-001", "알림 연결을 실패했습니다.", HttpStatus.BAD_REQUEST),
    FAILED_SSE_TRANSMISSION("Notification-002", "알림 전송을 실패했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);

    private final String code;
    private final String message;
    private final HttpStatus httpStatus;
}
