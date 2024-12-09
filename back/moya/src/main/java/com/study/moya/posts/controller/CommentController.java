package com.study.moya.posts.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.dto.comment.CommentCreateRequest;
import com.study.moya.posts.dto.comment.CommentResponse;
import com.study.moya.posts.dto.comment.CommentUpdateRequest;
import com.study.moya.posts.service.CommentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/posts/{postId}/comments")
@RequiredArgsConstructor
public class CommentController {

    private final CommentService commentService;

    /**
     * 댓글 작성
     */
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> addComment(@PathVariable Long postId,
                                                        @RequestBody CommentCreateRequest request,
                                                        @AuthenticationPrincipal Member member) {
        Long authorId = (member != null) ? member.getId() : null;
        Long commentId = commentService.addComment(postId, request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(commentId));
    }

    /**
     * 댓글 수정
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @RequestBody CommentUpdateRequest request,
                                                           @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        commentService.updateComment(postId, commentId, request, currentUserId);
        return ResponseEntity.ok(ApiResponse.success());
    }

    /**
     * 댓글 삭제
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long postId,
                                              @PathVariable Long commentId,
                                              @AuthenticationPrincipal Member member) {
        Long currentUserId = (member != null) ? member.getId() : null;
        commentService.deleteComment(postId, commentId, currentUserId);
        return ResponseEntity.noContent().build();
    }

    /**
     * 댓글 목록 조회
     * 게시글 상세 조회 시 함께 내려줄 수도 있지만 별도 API도 고민 중이다..
     */
    @GetMapping
    public ResponseEntity<ApiResponse<List<CommentResponse>>> getComments(@PathVariable Long postId) {
        List<CommentResponse> comments = commentService.getCommentsByPostId(postId);
        return ResponseEntity.ok(ApiResponse.of(comments));
    }
}