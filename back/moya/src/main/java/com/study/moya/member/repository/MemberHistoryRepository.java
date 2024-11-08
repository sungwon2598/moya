package com.study.moya.member.repository;

import com.study.moya.member.domain.history.MemberHistory;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MemberHistoryRepository extends JpaRepository<MemberHistory, Long> {
    List<MemberHistory> findByMemberIdOrderByCreatedAtDesc(Long memberId);
    Optional<MemberHistory> findTopByMemberIdOrderByCreatedAtDesc(Long memberId);
}