package com.study.moya.token.service.ticket;

import com.study.moya.token.dto.ticket.TicketTransactionDto;
import com.study.moya.token.repository.ticket.TicketTransactionRepository;
import com.study.moya.token.repository.ticket.TicketAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketTransactionService {

    private final TicketTransactionRepository ticketTransactionRepository;
    private final TicketAccountRepository ticketAccountRepository;

    /**
     * 회원의 티켓 거래 내역 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<TicketTransactionDto> getMemberTransactions(Long memberId, Pageable pageable) {
        log.info("회원 티켓 거래 내역 조회. 멤버 ID: {}, 페이지: {}", memberId, pageable.getPageNumber());

        Long ticketAccountId = getTicketAccountId(memberId);

        return ticketTransactionRepository.findByTicketAccountIdOrderByCreatedAtDesc(ticketAccountId, pageable)
                .map(TicketTransactionDto::from);
    }

    /**
     * 특정 기간 동안의 회원 티켓 거래 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TicketTransactionDto> getMemberTransactionsBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        log.info("기간별 회원 티켓 거래 내역 조회. 멤버 ID: {}, 기간: {} ~ {}", memberId, start, end);

        Long ticketAccountId = getTicketAccountId(memberId);

        return ticketTransactionRepository.findByTicketAccountIdAndCreatedAtBetweenOrderByCreatedAtDesc(
                        ticketAccountId, start, end)
                .stream()
                .map(TicketTransactionDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 최근 거래 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TicketTransactionDto> getRecentTransactions(Long memberId, int limit) {
        log.info("최근 티켓 거래 내역 조회. 멤버 ID: {}, 제한: {}", memberId, limit);

        Long ticketAccountId = getTicketAccountId(memberId);
        Pageable pageable = PageRequest.of(0, limit);

        return ticketTransactionRepository.findByTicketAccountIdOrderByCreatedAtDesc(ticketAccountId, pageable)
                .getContent()
                .stream()
                .map(TicketTransactionDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 티켓 계정 ID 조회
     */
    private Long getTicketAccountId(Long memberId) {
        return ticketAccountRepository.findByMemberId(memberId)
                .map(account -> account.getId())
                .orElse(null);
    }
}