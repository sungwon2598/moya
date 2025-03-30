package com.study.moya.posts.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.posts.domain.Comment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {
    int countByAuthor(Member member);

    List<Comment> findByAuthor(@Param("author") Member author);
}
