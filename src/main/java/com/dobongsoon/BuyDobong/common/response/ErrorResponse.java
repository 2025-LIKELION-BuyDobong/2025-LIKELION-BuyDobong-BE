package com.dobongsoon.BuyDobong.common.response;

import lombok.*;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ErrorResponse {
    private boolean isSuccess;
    private int status;
    private String error;
    private String message;
}