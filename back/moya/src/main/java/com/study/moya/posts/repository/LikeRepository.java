package com.study.moya.posts.repository;

import com.study.moya.posts.domain.Like;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LikeRepository extends JpaRepository<Like, Long> {
}
