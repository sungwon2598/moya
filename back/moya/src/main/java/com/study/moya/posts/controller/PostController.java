package com.study.moya.posts.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.dto.like.LikeResponse;
import com.study.moya.posts.dto.post.PostCreateRequest;
import com.study.moya.posts.dto.post.PostDetailResponse;
import com.study.moya.posts.dto.post.PostListResponse;
import com.study.moya.posts.dto.post.PostUpdateRequest;
import com.study.moya.posts.service.PostService;
import com.study.moya.posts.util.MemberCheckUtils;
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
    public ResponseEntity<Long> createPost(@RequestBody PostCreateRequest request,
                                                        @AuthenticationPrincipal Member member) {
        Long authorId = MemberCheckUtils.getMemberId(member);
        Long postId = postService.createPost(request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(postId);
    }

    /**
     * 게시글 목록 조회 (페이징)
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<PostListResponse>>> getPostList(
            @RequestParam(defaultValue = "0") int page,
            @AuthenticationPrincipal Member member) {
        Long currentUserId = MemberCheckUtils.getMemberId(member);
        ApiResponse<List<PostListResponse>> response = postService.getPostList(page, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 상세 조회
     */
    @GetMapping("/{postId}")
    public ResponseEntity<ApiResponse<PostDetailResponse>> getPostDetail(@PathVariable Long postId,
                                                                         @AuthenticationPrincipal Member member) {
        Long currentUserId = MemberCheckUtils.getMemberId(member);
        ApiResponse<PostDetailResponse> response = postService.getPostDetail(postId, currentUserId);
        return ResponseEntity.ok(response);
    }

    /**
     * 게시글 수정
     */
    @PostMapping("/{postId}")
    public ResponseEntity<ApiResponse<Void>> updatePost(@PathVariable Long postId,
                                                        @RequestBody PostUpdateRequest request,
                                                        @AuthenticationPrincipal Member member) {
        Long authorId = MemberCheckUtils.getMemberId(member);
        postService.updatePost(postId, request, authorId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 게시글 삭제
     */
    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Long postId,
                                           @AuthenticationPrincipal Member member) {
        Long authorId = MemberCheckUtils.getMemberId(member);
        boolean isAdmin = MemberCheckUtils.isAdmin(member);

        if (isAdmin) {
            // 관리자가 삭제 요청한 경우
            postService.deletePostAsAdmin(postId);
        } else {
            // 일반 사용자가 삭제 요청한 경우
            postService.deletePost(postId, authorId);
        }

        return ResponseEntity.noContent().build();
    }

    /**
     * 좋아요 추가
     */
    @PostMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> addLike(@PathVariable Long postId,
                                                             @AuthenticationPrincipal Member member) {
        Long currentUserId = MemberCheckUtils.getMemberId(member);
        LikeResponse likeResponse = postService.addLike(postId, currentUserId);
        return ResponseEntity.ok(ApiResponse.of(likeResponse));
    }

    /**
     * 좋아요 취소
     */
    @DeleteMapping("/{postId}/like")
    public ResponseEntity<ApiResponse<LikeResponse>> removeLike(@PathVariable Long postId,
                                                                @AuthenticationPrincipal Member member) {
        Long currentUserId = MemberCheckUtils.getMemberId(member);
        LikeResponse likeResponse = postService.removeLike(postId, currentUserId);
        return ResponseEntity.ok(ApiResponse.of(likeResponse));
    }
}
