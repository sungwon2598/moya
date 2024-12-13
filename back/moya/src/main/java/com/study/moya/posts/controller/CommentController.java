package com.study.moya.posts.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.dto.comment.CommentCreateRequest;
import com.study.moya.posts.dto.comment.CommentUpdateRequest;
import com.study.moya.posts.service.CommentService;
import com.study.moya.posts.util.MemberCheckUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

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
        Long authorId = MemberCheckUtils.getMemberId(member);
        Long commentId = commentService.addComment(postId, request, authorId);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.of(commentId));
    }

    /**
     * 댓글 수정
     */
    @PostMapping("/{commentId}")
    public ResponseEntity<ApiResponse<Void>> updateComment(@PathVariable Long postId,
                                                           @PathVariable Long commentId,
                                                           @RequestBody CommentUpdateRequest request,
                                                           @AuthenticationPrincipal Member member) {
        Long currentUserId = MemberCheckUtils.getMemberId(member);
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
        Long currentUserId = MemberCheckUtils.getMemberId(member);

        boolean isAdmin = MemberCheckUtils.isAdmin(member);

        if (isAdmin) {
            commentService.deleteCommentAsAdmin(postId, commentId, currentUserId);
        } else {
            commentService.deleteComment(postId, commentId, currentUserId);
        }

        return ResponseEntity.noContent().build();
    }

}