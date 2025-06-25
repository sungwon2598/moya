package com.study.moya.posts.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.global.api.ApiResponse;
import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Comment;
import com.study.moya.posts.domain.Like;
import com.study.moya.posts.domain.Post;
import com.study.moya.posts.dto.like.LikeResponse;
import com.study.moya.posts.dto.post.*;
import com.study.moya.posts.exception.PostErrorCode;
import com.study.moya.posts.exception.PostException;
import com.study.moya.posts.repository.LikeRepository;
import com.study.moya.posts.repository.PostRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.*;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final com.study.moya.member.repository.MemberRepository memberRepository;
    // 인메모리 캐시 저장소
    private volatile List<PopularListResponse> cachedPopularPosts = new ArrayList<>();
    private volatile LocalDateTime lastUpdated = null;

    private static final int CACHE_DURATION_MINUTES = 30;


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

    // ✅ 게시글 목록 조회 최적화
    @Transactional(readOnly = true)
    public ApiResponse<List<PostListResponse>> getPostList(int page, Long memberId) {
        PageRequest pageRequest = PageRequest.of(page, 20, Sort.by("createdAt").descending());
        Page<Post> postPage = postRepository.findByIsDeletedFalse(pageRequest);

        // 게시글 ID 목록 추출
        List<Long> postIds = postPage.getContent().stream()
                .map(Post::getId)
                .toList();

        // 배치로 좋아요 수 조회 (N+1 문제 해결)
        Map<Long, Integer> likeCountMap = getLikeCountsForPosts(postIds);

        // 현재 사용자의 좋아요 여부 배치 조회
        Map<Long, Boolean> memberLikeMap = getMemberLikeStatusForPosts(postIds, memberId);

        List<PostListResponse> responseList = postPage.getContent().stream().map(post -> {
            int commentCount = post.getComments().size();
            String authorName = post.getAuthor().getNickname();

            // 배치 조회 결과 사용
            int totalLikes = likeCountMap.getOrDefault(post.getId(), 0);
            boolean isLiked = memberLikeMap.getOrDefault(post.getId(), false);

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

    // ✅ 게시글 상세 조회 최적화
    @Transactional
    public ApiResponse<PostDetailResponse> getPostDetail(Long postId, Long memberId) {
        Post post = postRepository.findWithLockById(postId)
                .orElseThrow(() -> PostException.of(PostErrorCode.POST_NOT_FOUND));

        if (post.getIsDeleted()) {
            throw PostException.of(PostErrorCode.DELETED_POST);
        }

        // 조회수 증가
        post.incrementViews();
        String authorName = post.getAuthor().getNickname();

        // 좋아요 여부 및 개수 조회
        boolean isLiked = false;
        if (memberId != null) {
            isLiked = likeRepository.existsByMemberIdAndPostId(memberId, postId);
        }
        int totalLikes = likeRepository.countByPostId(postId);

        // 댓글 처리 (기존 로직 유지)
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
                totalLikes
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
    public LikeResponse toggleLike(Long postId, Long memberId) {
        if (memberId == null) {
            throw PostException.of(PostErrorCode.BAD_ACCESS);
        }

        if(!postRepository.existsById(postId)) {
            throw PostException.of(PostErrorCode.POST_NOT_FOUND);
        }

        if(!memberRepository.existsById(memberId)) {
            throw PostException.of(PostErrorCode.MEMBER_NOT_FOUND);
        }

        // 토글 로직: 삭제 시도 후 결과에 따라 분기
        int deletedCount = likeRepository.deleteByMemberIdAndPostId(memberId, postId);

        if (deletedCount > 0) {
            // 좋아요 취소됨
            int totalLikes = likeRepository.countByPostId(postId);
            return new LikeResponse(false, totalLikes);
        } else {
            // 좋아요 추가
            try {
                Like like = Like.builder()
                        .member(memberRepository.getReferenceById(memberId))  // 프록시 사용
                        .post(postRepository.getReferenceById(postId))        // 프록시 사용
                        .build();
                likeRepository.save(like);

                int totalLikes = likeRepository.countByPostId(postId);
                return new LikeResponse(true, totalLikes);

            } catch (DataIntegrityViolationException e) {
                // 동시성으로 인한 중복 생성 시 (DB 제약 조건 활용)
                int totalLikes = likeRepository.countByPostId(postId);
                return new LikeResponse(true, totalLikes);
            }
        }
    }

    /**
     * 5분마다 인기 글 저장 (조회수 기준)
     */
    @Transactional(readOnly = true)
    @Scheduled(fixedRate = 1800000) // 30분
    public void refreshPopularPosts() {
        try {
            // 인기글 Top 10 조회
            List<Post> popularPosts = postRepository.findTop10ByIsDeletedFalseOrderByViewsDesc(Pageable.ofSize(10));

            // 응답 객체로 변환
            List<PopularListResponse> postResponses = popularPosts.stream()
                    .map(PopularListResponse::from)
                    .toList();

            // 인메모리에 저장 (원자적 업데이트)
            this.cachedPopularPosts = postResponses;
            this.lastUpdated = LocalDateTime.now();

            log.info("인메모리 캐시에 인기 게시글 업데이트 완료 - {} 개", postResponses.size());
            for (PopularListResponse post : postResponses) {
                log.info("인메모리 캐시에 게시글 id {}", post.postId());
            }


        } catch (Exception e) {
            log.error("인기 게시글 캐시 업데이트 중 오류 발생", e);
        }
    }

    /**
     * 인메모리에서 인기 글 조회
     */
    public List<PopularListResponse> getPopularPosts() {
        // 캐시가 비어있거나 만료되었으면 새로고침
        if (isCacheExpired()) {
            log.info("캐시가 만료되어 새로고침 실행");
            refreshPopularPosts();
        }

        // 방어적 복사로 반환
        return new ArrayList<>(cachedPopularPosts);
    }

    /**
     * 캐시 만료 여부 확인
     */
    private boolean isCacheExpired() {
        return cachedPopularPosts.isEmpty()
                || lastUpdated == null
                || lastUpdated.isBefore(LocalDateTime.now().minusMinutes(CACHE_DURATION_MINUTES));
    }

    // ✅ 배치 좋아요 수 조회 헬퍼 메서드
    private Map<Long, Integer> getLikeCountsForPosts(List<Long> postIds) {
        if (postIds.isEmpty()) {
            return Map.of();
        }

        // 한 번의 쿼리로 모든 게시글의 좋아요 수 조회
        List<Object[]> results = likeRepository.findLikeCountsByPostIds(postIds);

        return results.stream()
                .collect(Collectors.toMap(
                        result -> (Long) result[0],      // postId
                        result -> ((Number) result[1]).intValue() // likeCount
                ));
    }

    // ✅ 사용자 좋아요 여부 배치 조회 헬퍼 메서드
    private Map<Long, Boolean> getMemberLikeStatusForPosts(List<Long> postIds, Long memberId) {
        if (postIds.isEmpty() || memberId == null) {
            return postIds.stream()
                    .collect(Collectors.toMap(
                            postId -> postId,
                            postId -> false
                    ));
        }

        // 한 번의 쿼리로 사용자가 좋아요한 게시글 ID 목록 조회
        List<Long> likedPostIds = likeRepository.findLikedPostIdsByMemberAndPosts(memberId, postIds);
        Set<Long> likedPostIdSet = new HashSet<>(likedPostIds);

        return postIds.stream()
                .collect(Collectors.toMap(
                        postId -> postId,
                        likedPostIdSet::contains
                ));
    }


}