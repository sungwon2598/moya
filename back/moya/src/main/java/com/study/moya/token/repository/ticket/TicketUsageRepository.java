package com.study.moya.token.repository.ticket;

import com.study.moya.token.domain.enums.AiUsageStatus;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.ticket.TicketUsage;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface TicketUsageRepository extends JpaRepository<TicketUsage, Long> {

    List<TicketUsage> findByMemberIdAndStatus(Long memberId, AiUsageStatus status);

    List<TicketUsage> findByMemberIdAndTicketType(Long memberId, TicketType ticketType);

    Optional<TicketUsage> findByIdAndMemberId(Long id, Long memberId);

    Page<TicketUsage> findByMemberIdOrderByCreatedAtDesc(Long memberId, Pageable pageable);

    List<TicketUsage> findByMemberIdAndCreatedAtBetweenOrderByCreatedAtDesc(
            Long memberId, LocalDateTime start, LocalDateTime end);

    List<TicketUsage> findByStatus(AiUsageStatus status);
}
