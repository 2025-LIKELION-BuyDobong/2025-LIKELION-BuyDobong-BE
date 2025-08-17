package com.dobongsoon.BuyDobong.common.response;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

@RequiredArgsConstructor
@Getter
public enum ErrorCode {
    /**
     * 400 BAD_REQUEST - 잘못된 요청
     */
    BAD_REQUEST(HttpStatus.BAD_REQUEST, "잘못된 요청입니다."),

    /**
     * 401 UNAUTHORIZED - 인증 실패
     */
    TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "토큰이 만료되었습니다."),
    INVALID_ACCESS_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 토큰입니다."),
    INVALID_REFRESH_TOKEN(HttpStatus.UNAUTHORIZED, "유효하지 않은 Refresh 토큰입니다."),
    TOKEN_MISSING(HttpStatus.UNAUTHORIZED, "요청 헤더에 토큰이 없습니다."),

    /**
     * 403 FORBIDDEN - 권한 없음
     */
    FORBIDDEN(HttpStatus.FORBIDDEN, "권한이 없습니다."),
    MERCHANT_ONLY(HttpStatus.FORBIDDEN, "상점 등록은 상인만 가능합니다."),
    STORE_ALREADY_EXISTS(HttpStatus.CONFLICT, "이미 상점을 등록했습니다."),


    /**
     * 404 NOT_FOUND - 요청한 리소스를 찾을 수 없음
     */
    USER_NOT_FOUND(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
    STORE_NOT_FOUND(HttpStatus.NOT_FOUND, "상점을 찾을 수 없습니다."),
    PRODUCT_NOT_FOUND(HttpStatus.NOT_FOUND, "상품을 찾을 수 없습니다."),

    /**
     * 406 NOT_ACCEPTABLE - 허용되지 않는 요청 형식
     */


    /**
     * 409 CONFLICT - 요청 충돌
     */
    DUPLICATE_USERNAME(HttpStatus.CONFLICT, "중복된 닉네임입니다. 다른 닉네임을 입력해주세요."),

    /**
     * 500 INTERNAL_SERVER_ERROR - 서버 내부 오류
     */
    INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "서버 내부 오류가 발생했습니다."),

    /**
     * 502 BAD_GATEWAY - 이트웨이 또는 프록시 서버 오류
     */

    SAMPLE_EXCEPTION(HttpStatus.BAD_REQUEST, "샘플 예외입니다.");

    private final HttpStatus status;
    private final String message;

    public ErrorResponse getReasonHttpStatus() {
        return ErrorResponse.builder()
                .message(message)
                .status(status.value())
                .isSuccess(false)
                .error(this.name())
                .build();
    }
}