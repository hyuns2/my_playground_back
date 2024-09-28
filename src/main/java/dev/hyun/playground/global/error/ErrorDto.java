package dev.hyun.playground.global.error;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorDto {
    private final String name;
    private final String code;
    private final String message;
    private final HttpStatus httpStatus;

    public ErrorDto(CustomErrorCode errorCode) {
        this.name = errorCode.name();
        this.code = errorCode.getCode();
        this.message = errorCode.getMessage();
        this.httpStatus = errorCode.getHttpStatus();
    }
}
