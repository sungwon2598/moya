package com.study.moya.posts.service;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
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
    private final MemberRepository memberRepository;


    @Transactional
    public Long createPost(PostCreateRequest request, String email) {
        if (email == null) {
            throw PostException.of(PostErrorCode.BLANK_AUTHOR_EMAIL);
        }

        Member author = memberRepository.findByEmail(email)
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
    public ApiResponse<List<PostListResponse>> getPostList(int page, String email) {

        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByIsDeletedFalse(pageRequest);

        List<PostListResponse> responseList = postPage.getContent().stream().map(post -> {
            int commentCount = post.getComments().size();
            String authorName = post.getAuthor().getNickname();

            //좋아요 처리
            int totalLikes = likeRepository.countByPostId(post.getId());
            boolean isLiked = false;

            if (email != null) {
                Member member = memberRepository.findByEmail(email).orElse(null);
                if (member != null) {
                    isLiked = likeRepository.findByMemberIdAndPostId(member.getId(), post.getId()).isPresent();
                } else {
                    log.warn("접근한 사용자가 DB에서는 존재하지 않는 사용자인데, @AuthenticationPrincipal 안에 Email이 존재");
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
        // 여기서 ApiResponse.of(Page<T>) 사용으로 pagination 정보를 함께 담아 응답
    }

    @Transactional
    public ApiResponse<PostDetailResponse> getPostDetail(Long postId, String currentEmail) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw PostException.of(PostErrorCode.DELETED_POST);
        }

        //조회수
        post.incrementViews();
        //사용자
        String authorName = post.getAuthor().getNickname();

        Member member = null;
        if (currentEmail != null) {
            member = memberRepository.findByEmail(currentEmail).orElse(null);
        }

        // 좋아요 여부
        boolean isLiked = false;
        if (member != null) {
            isLiked = likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();
        }

        int totalLikes = likeRepository.countByPostId(postId);


        Set<Comment> allComments = post.getComments();
        // 루트 댓글만 필터
        List<Comment> rootComments = allComments.stream()
                .filter(c -> c.getParentComment() == null)
                .toList();

        // rootComments를 DTO로 변환
        List<PostDetailResponse.CommentDetail> commentDetails = rootComments.stream()
                .map(this::toCommentDetail)
                .toList();

        // 전체 댓글 수 (대댓글 포함)
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
                totalLikes
        );

        return ApiResponse.of(detailResponse);
    }

    private PostDetailResponse.CommentDetail toCommentDetail(Comment comment) {
        String commentAuthorName = comment.getAuthor().getNickname();

        // 대댓글 DTO 변환
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
    public void updatePost(Long postId, PostUpdateRequest request, String email) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getEmail().equals(email)) {
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
    public void deletePost(Long postId, String currentEmail) {
        if (currentEmail == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (!post.getAuthor().getEmail().equals(currentEmail)) {
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

    /**
     * 좋아요 추가
     */
    @Transactional
    public LikeResponse addLike(Long postId, String currentEmail) {
        if (currentEmail == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        Member member = memberRepository.findByEmail(currentEmail)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        boolean alreadyLiked = likeRepository.findByMemberIdAndPostId(member.getId(), postId).isPresent();

        if (alreadyLiked) {
            // throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
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

    /**
     * 좋아요 취소
     */
    @Transactional
    public LikeResponse removeLike(Long postId, String currentEmail) {
        if (currentEmail == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        Member member = memberRepository.findByEmail(currentEmail)
                .orElseThrow(() -> PostException.of(PostErrorCode.MEMBER_NOT_FOUND));

        Like like = likeRepository.findByMemberIdAndPostId(member.getId(), postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.NO_LIKED));

        likeRepository.delete(like);

        int totalLikes = likeRepository.countByPostId(postId);

        return new LikeResponse(false, totalLikes);
    }
}