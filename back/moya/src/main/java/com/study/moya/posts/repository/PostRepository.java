package com.study.moya.posts.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Post;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
    Page<Post> findByIsDeletedFalse(Pageable pageable);

    int countByAuthor(Member member);

    List<Post> findByAuthor(@Param("author") Member author);
}
