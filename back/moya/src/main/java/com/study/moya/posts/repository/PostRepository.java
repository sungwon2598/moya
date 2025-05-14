package com.study.moya.posts.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Post;
import jakarta.persistence.LockModeType;
import jakarta.persistence.QueryHint;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsDeletedFalse(Pageable pageable);

    int countByAuthor(Member member);

    List<Post> findByAuthor(@Param("author") Member author);

    @Query("SELECT p FROM Post p WHERE p.isDeleted = false ORDER BY p.views DESC")
    List<Post> findTop10ByIsDeletedFalseOrderByViewsDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @QueryHints({@QueryHint(name = "jakarta.persistence.lock.timeout", value = "3000")}) // 3초 락 타임아웃 (선택 사항)
    Optional<Post> findWithLockById(Long postId);

}
