package com.study.moya.posts.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    boolean existsByMemberIdAndPostId(Long memberId, Long postId);

    int countByPostId(Long postId);

    @Modifying //Modifying 은 반환 타입을 boolean을 지원하지 않는다.(영향 받은 행의 갯수)
    @Query("DELETE FROM Like l WHERE l.member.id = :memberId AND l.post.id = :postId")
    int deleteByMemberIdAndPostId(@Param("memberId") Long memberId, @Param("postId") Long postId);

    // ✅ 배치 좋아요 수 조회 (N+1 문제 해결)
    @Query("SELECT l.post.id, COUNT(l) FROM Like l WHERE l.post.id IN :postIds GROUP BY l.post.id")
    List<Object[]> findLikeCountsByPostIds(@Param("postIds") List<Long> postIds);

    // ✅ 사용자가 좋아요한 게시글 ID 목록 조회
    @Query("SELECT l.post.id FROM Like l WHERE l.member.id = :memberId AND l.post.id IN :postIds")
    List<Long> findLikedPostIdsByMemberAndPosts(@Param("memberId") Long memberId, @Param("postIds") List<Long> postIds);

    int countByMember(Member member);

    List<Like> findByMember(@Param("member") Member member);
}
