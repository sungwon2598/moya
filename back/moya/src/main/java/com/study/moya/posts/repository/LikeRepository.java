package com.study.moya.posts.repository;

import com.study.moya.posts.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LikeRepository extends JpaRepository<Like, Long> {
    Optional<Like> findByMemberIdAndPostId(Long memberId, Long postId);

    int countByPostId(Long postId);
}
