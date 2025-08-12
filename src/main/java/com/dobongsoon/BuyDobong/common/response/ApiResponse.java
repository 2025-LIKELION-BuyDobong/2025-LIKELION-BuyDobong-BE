package com.dobongsoon.BuyDobong.common.response;

import lombok.*;

@Getter
@AllArgsConstructor
@Builder
public class ApiResponse<T> {
    private boolean isSuccess;
    private int status;
    private String code;
    private String message;
    private T data;

    public static <T> ApiResponse<T> of(ResponseCode code, T data) {
        return ApiResponse.<T>builder()
                .isSuccess(true)
                .status(code.getStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .data(data)
                .build();
    }

    public static <T> ApiResponse<T> error(ErrorCode code) {
        return ApiResponse.<T>builder()
                .isSuccess(false)
                .status(code.getStatus().value())
                .code(code.name())
                .message(code.getMessage())
                .build();
    }
}
