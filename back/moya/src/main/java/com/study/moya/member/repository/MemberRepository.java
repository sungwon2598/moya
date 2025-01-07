package com.study.moya.member.repository;

import com.study.moya.member.domain.Member;
import java.util.Optional;

import com.study.moya.member.domain.MemberStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByNickname(String nickname);

    boolean existsByNicknameAndStatusNot(String nickname, MemberStatus status);
}