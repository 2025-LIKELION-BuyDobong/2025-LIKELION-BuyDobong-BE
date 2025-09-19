package com.dobongsoon.BuyDobong.domain.keyword.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class KeywordRequest {

    @NotBlank(message = "키워드는 비어 있을 수 없습니다.")
    private String word;
}