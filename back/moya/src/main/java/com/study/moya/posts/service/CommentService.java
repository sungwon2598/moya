package com.study.moya.posts.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.posts.domain.Comment;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.dto.comment.CommentCreateRequest;
import com.study.moya.posts.dto.comment.CommentUpdateRequest;
import com.study.moya.posts.exception.PostErrorCode;
import com.study.moya.posts.exception.PostException;
import com.study.moya.posts.repository.CommentRepository;
import com.study.moya.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long addComment(Long postId, CommentCreateRequest request, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> PostException.of(PostErrorCode.CANT_REPLY));
        }

        Comment comment = Comment.builder()
                .author(author)
                .post(post)
                .content(request.content())
                .parentComment(parentComment)
                .build();

        Comment saved = commentRepository.save(comment);
        return saved.getId();
    }

    @Transactional
    public void updateComment(Long postId, Long commentId, CommentUpdateRequest request, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        if (postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw PostException.of(PostErrorCode.INVALID_AUTHOR);
        }

        comment.changeContent(request.content());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        if (postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw PostException.of(PostErrorCode.INVALID_AUTHOR);
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentAsAdmin(Long postId, Long commentId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        if (postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        commentRepository.delete(comment);
    }
}