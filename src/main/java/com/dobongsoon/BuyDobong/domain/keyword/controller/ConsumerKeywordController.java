package com.dobongsoon.BuyDobong.domain.keyword.controller;

import com.dobongsoon.BuyDobong.common.exception.BusinessException;
import com.dobongsoon.BuyDobong.common.response.ErrorCode;
import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordRegisterResponse;
import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordRequest;
import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.Consumer;
import com.dobongsoon.BuyDobong.domain.keyword.model.ConsumerKeyword;
import com.dobongsoon.BuyDobong.domain.consumer.repository.ConsumerRepository;
import com.dobongsoon.BuyDobong.domain.keyword.service.ConsumerKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/keyword")
public class ConsumerKeywordController {

    private final ConsumerKeywordService consumerKeywordService;
    private final ConsumerRepository consumerRepository;

    private Long consumerIdOrThrow(Long userId) {
        return consumerRepository.findByUser_Id(userId)
                .map(Consumer::getId)
                .orElseThrow(() -> new BusinessException(ErrorCode.CONSUMER_NOT_FOUND));
    }

    @Operation(
            summary = "관심 키워드 등록",
            description = """
    특정 소비자의 관심 키워드를 등록합니다.
    - 인증 필요: CONSUMER
    - 요청: word (키워드 문자열)
    - 응답: 등록된 키워드의 정보 (id, word, createdAt 등)
    """
    )
    @PostMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<KeywordRegisterResponse> addKeyword(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid KeywordRequest request
    ) {
        Long consumerId = consumerIdOrThrow(userId);

        ConsumerKeyword saved = consumerKeywordService.add(consumerId, request.getWord());

        KeywordRegisterResponse body = KeywordRegisterResponse.builder()
                .id(saved.getKeyword().getId())       // Keyword 테이블 PK
                .consumerId(consumerId)
                .word(saved.getKeyword().getWord())
                .createdAt(saved.getCreatedAt())
                .success(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(
            summary = "관심 키워드 목록 조회",
            description = """
    특정 소비자가 등록한 관심 키워드 전체를 조회합니다.
    - 인증 필요: CONSUMER
    - 응답: 키워드 리스트 (id, word, createdAt)
    """
    )
    @GetMapping
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<List<KeywordResponse>> getKeywords(
            @AuthenticationPrincipal Long userId
    ) {
        Long consumerId = consumerIdOrThrow(userId);

        List<ConsumerKeyword> rows = consumerKeywordService.list(consumerId);
        List<KeywordResponse> result = rows.stream()
                .map(ck -> KeywordResponse.builder()
                        .id(ck.getKeyword().getId())           // Keyword 테이블 PK
                        .word(ck.getKeyword().getWord())
                        .createdAt(ck.getCreatedAt())
                        .build())
                .toList();
        return ResponseEntity.ok(result);
    }

    @Operation(
            summary = "관심 키워드 삭제",
            description = """
    특정 소비자의 관심 키워드를 삭제합니다.
    - 인증 필요: CONSUMER
    - 요청: keywordId (삭제할 키워드 ID (PathVariable))
    - 응답: 204 No Content
    """
    )
    @DeleteMapping("/{keywordId}")
    @PreAuthorize("hasRole('CONSUMER')")
    public ResponseEntity<Void> removeKeyword(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long keywordId
    ) {
        Long consumerId = consumerIdOrThrow(userId);
        consumerKeywordService.remove(consumerId, keywordId);
        return ResponseEntity.noContent().build();
    }
}