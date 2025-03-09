package com.study.moya.member.repository;

import com.study.moya.member.domain.Member;
import java.util.Optional;

import com.study.moya.member.domain.MemberStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndStatusNot(String nickname, MemberStatus status);

    Page<Member> findByIdGreaterThanEqual(Long startId, Pageable pageable);
}