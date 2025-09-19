package com.dobongsoon.BuyDobong.domain.keyword.controller;

import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordRegisterResponse;
import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordRequest;
import com.dobongsoon.BuyDobong.domain.keyword.dto.KeywordResponse;
import com.dobongsoon.BuyDobong.domain.keyword.model.UserKeyword;
import com.dobongsoon.BuyDobong.domain.keyword.service.UserKeywordService;
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
@RequestMapping("/api/keyword")
public class UserKeywordController {

    private final UserKeywordService userKeywordService;

    @Operation(
            summary = "관심 키워드 등록",
            description = """
    사용자의 관심 키워드를 등록합니다.
    - 인증 필요
    - 요청: word (키워드 문자열)
    - 응답: 등록된 키워드의 정보 (id, word, createdAt 등)
    """
    )
    @PostMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<KeywordRegisterResponse> addKeyword(
            @AuthenticationPrincipal Long userId,
            @RequestBody @Valid KeywordRequest request
    ) {
        UserKeyword saved = userKeywordService.add(userId, request.getWord());

        KeywordRegisterResponse body = KeywordRegisterResponse.builder()
                .id(saved.getKeyword().getId())       // Keyword 테이블 PK
                .userId(userId)
                .word(saved.getKeyword().getWord())
                .createdAt(saved.getCreatedAt())
                .success(true)
                .build();

        return ResponseEntity.status(HttpStatus.CREATED).body(body);
    }

    @Operation(
            summary = "관심 키워드 목록 조회",
            description = """
    사용자가 등록한 관심 키워드 전체를 조회합니다.
    - 인증 필요
    - 응답: 키워드 리스트 (id, word, createdAt)
    """
    )
    @GetMapping
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<KeywordResponse>> getKeywords(
            @AuthenticationPrincipal Long userId
    ) {
        List<UserKeyword> rows = userKeywordService.list(userId);
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
    사용자의 관심 키워드를 삭제합니다.
    - 인증 필요
    - 요청: keywordId (삭제할 키워드 ID (PathVariable))
    - 응답: 204 No Content
    """
    )
    @DeleteMapping("/{keywordId}")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Void> removeKeyword(
            @AuthenticationPrincipal Long userId,
            @PathVariable Long keywordId
    ) {
        userKeywordService.remove(userId, keywordId);
        return ResponseEntity.noContent().build();
    }
}