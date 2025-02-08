package com.example.rootimpact.global.error;

import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class ErrorResponse {
    private final String message;
    private final int status;
    private final String code;
    private final LocalDateTime timestamp;

    public ErrorResponse(String message) {
        this.message = message;
        this.status = HttpStatus.BAD_REQUEST.value();
        this.code = "400";
        this.timestamp = LocalDateTime.now();
    }
}
