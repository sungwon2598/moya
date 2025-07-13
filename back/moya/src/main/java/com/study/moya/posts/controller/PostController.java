package com.study.moya.posts.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.posts.dto.like.LikeResponse;
import com.study.moya.posts.dto.post.*;
import com.study.moya.posts.service.PostService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
@Slf4j
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성
     */
    @PostMapping
    public ResponseEntity<Long> createPost(@RequestBody PostCreateRequest request,
                                           @AuthenticationPrincipal Long memberId) {
        Long postId = postService.createPost(request, memberId);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Long memberId) {
        log.info("member ID = {}", memberId);
        ApiResponse<List<PostListResponse>> response = postService.getPostList(page, memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@PathVariable Long postId,
                                                                         @AuthenticationPrincipal Long memberId) {
        log.info("Authenticated Principal ID: {}", memberId);
        ApiResponse<PostDetailResponse> response = postService.getPostDetail(postId, memberId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable Long postId,
                                                        @RequestBody PostUpdateRequest request,
                                                        @AuthenticationPrincipal Long memberId) {
        postService.updatePost(postId, request, memberId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal Long memberId) {

        // 관리자인 경우에는 id 검증 없이 바로 삭제 처리
        if (SecurityContextHolder.getContext().getAuthentication().getAuthorities()
                .stream().anyMatch(auth -> auth.getAuthority().equals("ROLE_ADMIN"))) {
            postService.deletePostAsAdmin(postId);
        } else {
            postService.deletePost(postId, memberId);
        }

        return ResponseEntity.noContent().build();
    }

    // ✅ 기존 addLike, removeLike 엔드포인트를 toggleLike로 통합
    @PostMapping("/{postId}/like")
    @Operation(summary = "좋아요/취소", description = "좋아요 상태를 토글합니다")
    public ResponseEntity<LikeResponse> toggleLike(
            @PathVariable Long postId,
            @AuthenticationPrincipal Long memberId) {
        LikeResponse response = postService.toggleLike(postId, memberId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/popular")
    public ResponseEntity<ApiResponse<List<PopularListResponse>>> getPopularPosts() {
        log.info("인기 게시글 조회");
        List<PopularListResponse> popularPosts = postService.getPopularPosts();
        return ResponseEntity.ok(ApiResponse.of(popularPosts));
    }
}