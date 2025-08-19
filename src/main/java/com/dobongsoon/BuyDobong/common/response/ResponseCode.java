package com.dobongsoon.BuyDobong.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ResponseCode {
    /**
     * Common
     */
    SUCCESS(HttpStatus.OK, "요청이 성공적으로 처리되었습니다."),

    /**
     * User
     */
    SUCCESS_LOGIN(HttpStatus.OK, "성공적으로 로그인을 했습니다.");

    private final HttpStatus status;
    private final String message;
}