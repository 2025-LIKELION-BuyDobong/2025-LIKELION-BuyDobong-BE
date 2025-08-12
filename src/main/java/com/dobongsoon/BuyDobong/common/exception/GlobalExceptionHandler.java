package com.dobongsoon.BuyDobong.common.exception;

import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.common.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    // @Valid 실패 → 400 (필요시 BindException도 함께 처리)
    @ExceptionHandler({MethodArgumentNotValidException.class, BindException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception e) {
        log.warn("Validation error", e);
        var code = ErrorCode.BAD_REQUEST;
        return ResponseEntity
                .status(code.getStatus())
                .body(code.getReasonHttpStatus());
    }

    // 도메인/비즈니스 예외 → ErrorCode 기반 응답
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException e) {
        var code = e.getErrorCode();
        log.warn("Business exception: {}", code.name(), e);
        return ResponseEntity
                .status(code.getStatus())
                .body(code.getReasonHttpStatus());
    }

    // 예상치 못한 예외 → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleAll(Exception e) {
        log.error("Unexpected error", e);
        var code = ErrorCode.INTERNAL_SERVER_ERROR;
        return ResponseEntity
                .status(code.getStatus())
                .body(code.getReasonHttpStatus());
    }
}