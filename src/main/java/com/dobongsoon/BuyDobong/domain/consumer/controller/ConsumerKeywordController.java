package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordRegisterResponse;
import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordRequest;
import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerKeyword;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerKeywordService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/consumer/{consumerId}/keyword")
public class ConsumerKeywordController {

    private final ConsumerKeywordService consumerKeywordService;

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
    public ResponseEntity<KeywordRegisterResponse> addKeyword(
            @PathVariable Long consumerId,
            @RequestBody @Valid KeywordRequest request
    ) {
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
    public ResponseEntity<List<KeywordResponse>> getKeywords(@PathVariable Long consumerId) {
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
    public ResponseEntity<Void> removeKeyword(
            @PathVariable Long consumerId,
            @PathVariable Long keywordId
    ) {
        consumerKeywordService.remove(consumerId, keywordId);
        return ResponseEntity.noContent().build();
    }
}