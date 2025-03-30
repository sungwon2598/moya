package com.study.moya.token.repository;

import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.AiService;
import com.study.moya.token.domain.AiUsage;
import com.study.moya.token.domain.enums.AiUsageStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AiUsageRepository extends JpaRepository<AiUsage, Long> {
    Page<AiUsage> findByMemberOrderByCreatedAtDesc(Member member, Pageable pageable);

    List<AiUsage> findByMemberAndAiServiceAndCreatedAtBetween(
            Member member,
            AiService aiService,
            LocalDateTime startDate,
            LocalDateTime endDate);

    List<AiUsage> findByStatusOrderByCreatedAtAsc(AiUsageStatus status);
}