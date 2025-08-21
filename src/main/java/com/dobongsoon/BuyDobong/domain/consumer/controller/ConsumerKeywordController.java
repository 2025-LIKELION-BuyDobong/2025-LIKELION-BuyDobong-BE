package com.dobongsoon.BuyDobong.domain.consumer.controller;

import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordRegisterResponse;
import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordRequest;
import com.dobongsoon.BuyDobong.domain.consumer.dto.KeywordResponse;
import com.dobongsoon.BuyDobong.domain.consumer.model.ConsumerKeyword;
import com.dobongsoon.BuyDobong.domain.consumer.service.ConsumerKeywordService;
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

    // 관심 키워드 등록
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

    // 관심 키워드 목록 조회
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

    // 관심 키워드 삭제
    @DeleteMapping("/{keywordId}")
    public ResponseEntity<Void> removeKeyword(
            @PathVariable Long consumerId,
            @PathVariable Long keywordId
    ) {
        consumerKeywordService.remove(consumerId, keywordId);
        return ResponseEntity.noContent().build();
    }
}