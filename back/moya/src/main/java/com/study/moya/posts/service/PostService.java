package com.study.moya.posts.service;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Comment;
import com.study.moya.posts.domain.Like;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.dto.like.LikeResponse;
import com.study.moya.posts.dto.post.PostCreateRequest;
import com.study.moya.posts.dto.post.PostDetailResponse;
import com.study.moya.posts.dto.post.PostListResponse;
import com.study.moya.posts.dto.post.PostUpdateRequest;
import com.study.moya.posts.exception.PostErrorCode;
import com.study.moya.posts.exception.PostException;
import com.study.moya.posts.repository.LikeRepository;
import com.study.moya.posts.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final com.study.moya.member.repository.MemberRepository memberRepository;

    @Transactional
    public Long createPost(PostCreateRequest request, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        Member author = memberRepository.findById(memberId)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        Post post = Post.builder()
                .author(author)
                .title(request.title())
                .content(request.content())
                .recruits(request.recruits())
                .expectedPeriod(request.expectedPeriod())
                .studies(request.studies())
                .studyDetails(request.studyDetails())
                .startDate(request.startDate())
                .endDate(request.endDate())
                .build();

        Post saved = postRepository.save(post);

        return saved.getId();
    }

    @Transactional
    public ApiResponse<List<PostListResponse>> getPostList(int page, Long memberId) {

        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByIsDeletedFalse(pageRequest);
        List<PostListResponse> responseList = postPage.getContent().stream().map(post -> {
            int commentCount = post.getComments().size();
            String authorName = post.getAuthor().getNickname();
            // 좋아요 처리
            int totalLikes = likeRepository.countByPostId(post.getId());
            boolean isLiked = false;

            if (memberId != null) {
                Member member = memberRepository.findById(memberId).orElse(null);
                if (member != null) {
                    isLiked = likeRepository.findByMemberIdAndPostId(member.getId(), post.getId()).isPresent();
                } else {
                    log.warn("인증된 사용자가 DB에 존재하지 않습니다. id: {}", memberId);
                }
            }
            return new PostListResponse(
                    post.getId(),
                    post.getTitle(),
                    post.getContent(),
                    post.getRecruits(),
                    post.getExpectedPeriod(),
                    post.getStartDate(),
                    post.getEndDate(),
                    post.getStudies(),
                    post.getStudyDetails(),
                    authorName,
                    post.getViews(),
                    commentCount,
                    isLiked,
                    totalLikes
            );
        }).toList();

        return ApiResponse.of(new PageImpl<>(responseList, pageRequest, postPage.getTotalElements()));
    }

    @Transactional
    public ApiResponse<PostDetailResponse> getPostDetail(Long postId, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw PostException.of(PostErrorCode.DELETED_POST);
        }

        // 조회수 증가
        post.incrementViews();
        String authorName = post.getAuthor().getNickname();

        Member member = null;
        if (memberId != null) {
            member = memberRepository.findById(memberId).orElse(null);
        }

        // 좋아요 여부
        boolean isLiked = member != null && likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();

        Set<Comment> allComments = post.getComments();
        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentComment() == null)
                .toList();

        List<PostDetailResponse.CommentDetail> commentDetails = rootComments.stream()
                .map(this::toCommentDetail)
                .toList();

        int totalComments = allComments.size();

        PostDetailResponse detailResponse = new PostDetailResponse(
                post.getId(),
                post.getTitle(),
                post.getContent(),
                post.getRecruits(),
                post.getExpectedPeriod(),
                post.getStartDate(),
                post.getEndDate(),
                post.getStudies(),
                post.getStudyDetails(),
                authorName,
                post.getViews(),
                commentDetails,
                totalComments,
                isLiked,
                likeRepository.countByPostId(postId)
        );

        return ApiResponse.of(detailResponse);
    }

    private PostDetailResponse.CommentDetail toCommentDetail(Comment comment) {
        String commentAuthorName = comment.getAuthor().getNickname();

        List<PostDetailResponse.CommentDetail.ReplyDetail> replyDetails = comment.getReplies().stream()
                .map(this::toReplyDetail)
                .toList();

        return new PostDetailResponse.CommentDetail(
                comment.getId(),
                commentAuthorName,
                comment.getContent(),
                comment.getCreatedAt(),
                replyDetails
        );
    }

    private PostDetailResponse.CommentDetail.ReplyDetail toReplyDetail(Comment reply) {
        return new PostDetailResponse.CommentDetail.ReplyDetail(
                reply.getId(),
                reply.getAuthor().getNickname(),
                reply.getContent(),
                reply.getCreatedAt()
        );
    }

    @Transactional
    public void updatePost(Long postId, PostUpdateRequest request, Long memberId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw PostException.of(PostErrorCode.INVALID_AUTHOR);
        }

        post.updatePost(
                request.title(),
                request.content(),
                request.recruits(),
                request.expectedPeriod(),
                request.studies(),
                request.studyDetails(),
                request.startDate(),
                request.endDate()
        );
    }

    @Transactional
    public void deletePost(Long postId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getId().equals(memberId)) {
            throw PostException.of(PostErrorCode.INVALID_AUTHOR);
        }

        post.markAsDeleted();
    }

    @Transactional
    public void deletePostAsAdmin(Long postId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        post.markAsDeleted();
    }

    @Transactional
    public LikeResponse addLike(Long postId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        boolean alreadyLiked = likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();

        if (alreadyLiked) {
            throw PostException.of(PostErrorCode.ALREADY_LIKED);
        }

        Like like = Like.builder()
                .member(member)
                .post(post)
                .build();
        likeRepository.save(like);

        int totalLikes = likeRepository.countByPostId(postId);

        return new LikeResponse(true, totalLikes);
    }

    @Transactional
    public LikeResponse removeLike(Long postId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        Like like = likeRepository.findByMemberIdAndPostId(member.getId(), postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_LIKED));

        likeRepository.delete(like);

        int totalLikes = likeRepository.countByPostId(postId);

        return new LikeResponse(false, totalLikes);
    }
}