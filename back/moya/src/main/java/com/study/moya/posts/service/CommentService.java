package com.study.moya.posts.service;

import com.study.moya.posts.dto.comment.CommentCreateRequest;
import com.study.moya.posts.dto.comment.CommentResponse;
import com.study.moya.posts.dto.comment.CommentUpdateRequest;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CommentService {
    public Long addComment(Long postId, CommentCreateRequest request, Long authorId) {
        return postId;
    }

    public void updateComment(Long postId, Long commentId, CommentUpdateRequest request, Long currentUserId) {

    }

    public void deleteComment(Long postId, Long commentId, Long currentUserId) {

    }

    public List<CommentResponse> getCommentsByPostId(Long postId) {
        return null;
    }
}
