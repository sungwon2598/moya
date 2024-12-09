package com.study.moya.posts.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.dto.like.LikeResponse;
import com.study.moya.posts.dto.post.PostCreateRequest;
import com.study.moya.posts.dto.post.PostDetailResponse;
import com.study.moya.posts.dto.post.PostListResponse;
import com.study.moya.posts.dto.post.PostUpdateRequest;
import com.study.moya.posts.service.CommentService;
import com.study.moya.posts.service.LikeService;
import com.study.moya.posts.service.PostService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts")
@RequiredArgsConstructor
public class PostController {

    private final PostService postService;

    /**
     * 게시글 생성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createPost(@RequestBody PostCreateRequest request,
                                                        @AuthenticationPrincipal Member member) {
        Long authorId = (member != null) ? member.getId() : null;
        Long postId = postService.createPost(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(postId));
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        ApiResponse<List<PostListResponse>> response = postService.getPostList(page, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@PathVariable Long postId,
                                                                         @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        ApiResponse<PostDetailResponse> response = postService.getPostDetail(postId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정
     */
    @PutMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable Long postId,
                                                        @RequestBody PostUpdateRequest request,
                                                        @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        postService.updatePost(postId, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        postService.deletePost(postId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 좋아요 추가
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> addLike(@PathVariable Long postId,
                                                             @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        LikeResponse likeResponse = postService.addLike(postId, currentUserId);
        return ResponseEntity.ok(ApiResponse.of(likeResponse));
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> removeLike(@PathVariable Long postId,
                                                                @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        LikeResponse likeResponse = postService.removeLike(postId, currentUserId);
        return ResponseEntity.ok(ApiResponse.of(likeResponse));
    }
}
