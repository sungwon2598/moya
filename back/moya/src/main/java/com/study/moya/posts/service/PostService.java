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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.annotation.Schedules;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final com.study.moya.member.repository.MemberRepository memberRepository;
    private final RedisTemplate<String, Object> redisTemplate;

    private static final String POPULAR_POSTS_KEY = "popular:posts";

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

    @Transactional(readOnly = true)
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
        Post post = postRepository.findWithLockById(postId)
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
                post.getLikes().size()
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

    /**
     * 5분마다 인기 글 저장 (조회수 기준)
     */
    @Transactional(readOnly = true)
    @Scheduled(fixedRate = 300000)
    public void refreshPopularPosts() {
        // 인기글 Top 10 조회 (인덱스 활용)
        List<Post> popularPosts = postRepository.findTop10ByIsDeletedFalseOrderByViewsDesc();

        // 레디스에 저장할 데이터 준비
        List<PopularListResponse> postResponses = popularPosts.stream()
                .map(PopularListResponse::from)
                .toList();

        redisTemplate.delete(POPULAR_POSTS_KEY);

        ObjectMapper objectMapper = new ObjectMapper();

        for (PopularListResponse post : postResponses) {
            try {
                // 객체를 Json으로 파싱
                String postJson = objectMapper.writeValueAsString(post);
                redisTemplate.opsForList().rightPush(POPULAR_POSTS_KEY, postJson);
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 오류: {}", e.getMessage());
            }
        }

        redisTemplate.expire(POPULAR_POSTS_KEY, 300, TimeUnit.SECONDS);

        log.info("Redis 캐시에 인기 게시글 업데이트 완료");
    }

    /**
     * Redis 에서 인기 글 조회 (조회수 기준)
     * @return 인기 글 목록
     */
    public List<PopularListResponse> getPopularPosts() {
        List<Object> cachedPosts = redisTemplate.opsForList().range(POPULAR_POSTS_KEY, 0, -1);

        if (cachedPosts == null || cachedPosts.isEmpty()) {
            log.info("인기 게시글 관련 레디스에 데이터가 없다. refresh 메서드 재요청");
            refreshPopularPosts();
            cachedPosts = redisTemplate.opsForList().range(POPULAR_POSTS_KEY, 0, -1);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        List<PopularListResponse> result = new ArrayList<>();

        for (Object cachedPost : cachedPosts) {
            try {
                // Json 을 객체로 파싱
                PopularListResponse post = objectMapper.readValue(cachedPost.toString(), PopularListResponse.class);
                result.add(post);
            } catch (JsonProcessingException e) {
                log.error("JSON 파싱 오류: {}", e.getMessage());
            }
        }

        return result;

    }


}