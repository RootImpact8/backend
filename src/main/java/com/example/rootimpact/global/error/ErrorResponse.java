package com.example.rootimpact.global.error;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ErrorResponse {
    private final String message;
    private final int status;
    private final String code;
    private final LocalDateTime timestamp;

    public static ErrorResponse of(ErrorCode errorCode) {
        return ErrorResponse.builder()
                       .message(errorCode.getMessage())
                       .status(errorCode.getHttpStatus().value())
                       .code(errorCode.name())
                       .timestamp(LocalDateTime.now())
                       .build();
    }
}
