package com.example.rootimpact.global.exception;

import com.example.rootimpact.global.error.ErrorCode;
import com.example.rootimpact.global.error.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(GlobalException.class)
    public ResponseEntity<ErrorResponse> handleCustomException(GlobalException e) {
        log.error("GlobalException: {}", e.getMessage());
        ErrorCode errorCode = e.getErrorCode();
        return ResponseEntity
                       .status(errorCode.getHttpStatus())
                       .body(ErrorResponse.of(errorCode));
    }
}
