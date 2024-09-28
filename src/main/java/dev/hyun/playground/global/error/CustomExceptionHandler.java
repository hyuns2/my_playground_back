package dev.hyun.playground.global.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class CustomExceptionHandler {
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ErrorDto> handleException(Exception e) {
        e.printStackTrace();
        CustomErrorCode errorCode = CustomErrorCode.SERVER_ERROR;
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorDto(errorCode));
    }

    @ExceptionHandler(AuthorizationDeniedException.class)
    protected ResponseEntity<ErrorDto> handleException(AuthorizationDeniedException e) {
        CustomErrorCode errorCode = CustomErrorCode.FORBIDDEN;
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorDto(errorCode));
    }

    @ExceptionHandler(CustomException.class)
    protected ResponseEntity<ErrorDto> handleException(CustomException e) {
        CustomErrorCode errorCode = e.getErrorCode();
        return ResponseEntity.status(errorCode.getHttpStatus().value())
                .body(new ErrorDto(errorCode));
    }
}
