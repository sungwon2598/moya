package com.study.moya.posts.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);

    int countByPostId(Long postId);

    int countByMember(Member member);

    List<Like> findByMember(@Param("member") Member member);
}
