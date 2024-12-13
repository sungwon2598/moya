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
import com.study.moya.posts.repository.LikeRepository;
import com.study.moya.posts.repository.PostRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PostService {

    private final PostRepository postRepository;
    private final LikeRepository likeRepository;
    private final MemberRepository memberRepository;


    @Transactional
    public Long createPost(PostCreateRequest request, Long authorId) {
        if (authorId == null) {
            throw new IllegalArgumentException("작성자의 아이디가 비어있습니다.");
        }

        Member author = memberRepository.findById(authorId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

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

            //좋아요 처리
            int totalLikes = likeRepository.countByPostId(post.getId());
            boolean isLiked = false;
            if (memberId != null) {
                isLiked = likeRepository.findByMemberIdAndPostId(memberId, post.getId()).isPresent();
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
    public ApiResponse<PostDetailResponse> getPostDetail(Long postId, Long currentUserId) {
        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        //조회수
        post.incrementViews();
        //사용자
        String authorName = post.getAuthor().getNickname();

        boolean isLiked = (currentUserId != null)
                && likeRepository.findByMemberIdAndPostId(currentUserId, postId).isPresent();

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

    public void updatePost(Long postId, PostUpdateRequest request, Long authorId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(authorId)) {
            throw new IllegalArgumentException("해당 게시글의 작성자가 아닙니다.");
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
    public void deletePost(Long postId, Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        if (!post.getAuthor().getId().equals(currentUserId)) {
            throw new IllegalArgumentException("해당 글의 작성자가 아닙니다.");
        }

        post.markAsDeleted();
    }

    @Transactional
    public void deletePostAsAdmin(Long postId) {
        Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Post not found"));

        post.markAsDeleted();
    }

    /**
     * 좋아요 추가
     */
    @Transactional
    public LikeResponse addLike(Long postId, Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        Post post = postRepository.findById(postId)
                .orElseThrow(() -> new IllegalArgumentException("Post not found"));

        Member member = memberRepository.findById(currentUserId)
                .orElseThrow(() -> new IllegalArgumentException("Member not found"));

        boolean alreadyLiked = likeRepository.findByMemberIdAndPostId(currentUserId, postId).isPresent();

        if (alreadyLiked) {
            throw new IllegalStateException("이미 좋아요를 눌렀습니다.");
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
    public LikeResponse removeLike(Long postId, Long currentUserId) {
        if (currentUserId == null) {
            throw new IllegalArgumentException("잘못된 접근입니다.");
        }

        Like like = likeRepository.findByMemberIdAndPostId(currentUserId, postId)
                .orElseThrow(() -> new IllegalArgumentException("사용자가 게시글에 대해 좋아요를 누른 적이 없습니다."));

        likeRepository.delete(like);

        int totalLikes = likeRepository.countByPostId(postId);

        return new LikeResponse(false, totalLikes);
    }
}