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

        // 게시글 존재 및 삭제 여부 확인
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw PostException.of(PostErrorCode.DELETED_POST);
        }

        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> PostException.of(PostErrorCode.CANT_REPLY));

            // 부모 댓글이 같은 게시글에 속하는지 확인
            if (!parentComment.getPost().getId().equals(postId)) {
                throw PostException.of(PostErrorCode.BAD_ACCESS);
            }
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

        if (!postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        // 댓글이 해당 게시글에 속하는지 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

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

        if (!postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        // 댓글이 해당 게시글에 속하는지 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        if (!comment.getAuthor().getId().equals(memberId)) {
            throw PostException.of(PostErrorCode.INVALID_AUTHOR);
        }

        // ✅ 대댓글이 있는 경우 처리 방안 고려
        if (!comment.getReplies().isEmpty()) {
            // 옵션 1: 대댓글이 있으면 삭제 금지
            // throw PostException.of(PostErrorCode.CANNOT_DELETE_COMMENT_WITH_REPLIES);

            // 옵션 2: 내용만 변경 (현재 구현)
            comment.changeContent("삭제된 댓글입니다.");
        } else {
            // 대댓글이 없으면 실제 삭제
            commentRepository.delete(comment);
        }
    }

    @Transactional
    public void deleteCommentAsAdmin(Long postId, Long commentId) {
        if (!postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_COMMENT));

        // 댓글이 해당 게시글에 속하는지 확인
        if (!comment.getPost().getId().equals(postId)) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        // ✅ 대댓글이 있는 경우 처리 방안 고려
        if (!comment.getReplies().isEmpty()) {
            // 옵션 1: 대댓글이 있으면 삭제 금지
            // throw PostException.of(PostErrorCode.CANNOT_DELETE_COMMENT_WITH_REPLIES);

            // 옵션 2: 내용만 변경 (현재 구현)
            comment.changeContent("관리자에 의해 가려진 댓글입니다.");
        } else {
            // 대댓글이 없으면 실제 삭제
            commentRepository.delete(comment);
        }
    }
}