package com.study.moya.token.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.TokenAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.stereotype.Repository;

import jakarta.persistence.LockModeType;
import java.util.Optional;

@Repository
public interface TokenAccountRepository extends JpaRepository<TokenAccount, Long> {
    Optional<TokenAccount> findByMember(Member member);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<TokenAccount> findWithLockById(Long id);

    boolean existsByMemberId(Long memberId);
}