package com.study.moya.posts.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.posts.domain.Comment;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.dto.comment.CommentCreateRequest;
import com.study.moya.posts.dto.comment.CommentUpdateRequest;
import com.study.moya.posts.repository.CommentRepository;
import com.study.moya.posts.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class CommentService {

    private final PostRepository postRepository;
    private final CommentRepository commentRepository;
    private final MemberRepository memberRepository;

    @Transactional
    public Long addComment(Long postId, CommentCreateRequest request, String email) {
        if (email == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post Not Found"));
        Member author = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Member Not Found"));

        Comment parentComment = null;
        if (request.parentCommentId() != null) {
            parentComment = commentRepository.findById(request.parentCommentId())
                    .orElseThrow(() -> new IllegalArgumentException("대댓글을 생성할 타겟 댓글이 존재하지않습니다."));
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
    public void updateComment(Long postId, Long commentId, CommentUpdateRequest request, String email) {
        if (email == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        if (postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하는 게시글이 아닙니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 권한 체크: 작성자 또는 관리자
        if (!comment.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("작성자만 수정 가능합니다.");
        }

        comment.changeContent(request.content());
    }

    @Transactional
    public void deleteComment(Long postId, Long commentId, String email) {
        if (email == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        if (postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하는 게시글이 아닙니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        // 권한 체크
        if (!comment.getAuthor().getEmail().equals(email)) {
            throw new IllegalArgumentException("작성자만 삭제 가능합니다.");
        }

        commentRepository.delete(comment);
    }

    @Transactional
    public void deleteCommentAsAdmin(Long postId, Long commentId, String email) {
        if (email == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        if (postRepository.existsById(postId)) {
            throw new IllegalArgumentException("존재하는 게시글이 아닙니다.");
        }

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }

}
