package com.study.moya.token.service.ticket;

import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.dto.ticket.TicketAccountDto;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import com.study.moya.token.dto.ticket.TicketUsageDto;
import com.study.moya.token.dto.ticket.reponse.TicketBalanceResponse;
import com.study.moya.token.dto.ticket.reponse.TicketUsageResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketFacadeService {

    private final TicketAccountService ticketAccountService;
    private final TicketTransactionService ticketTransactionService;
    private final TicketUsageService ticketUsageService;

    /**
     * 회원의 티켓 계정을 조회하거나 없으면 생성합니다.
     */
    public TicketAccountDto getOrCreateTicketAccount(Long memberId) {
        return ticketAccountService.getOrCreateTicketAccount(memberId);
    }

    /**
     * 회원의 티켓 잔액 및 최근 거래 내역을 조회합니다.
     */
    public TicketBalanceResponse getTicketBalance(Long memberId) {
        getOrCreateTicketAccount(memberId);
        return ticketAccountService.getTicketBalance(memberId);
    }

    /**
     * 특정 타입의 티켓 보유 여부 확인
     */
    public boolean hasTicket(Long memberId, TicketType ticketType) {
        log.info("티켓 보유 확인. 멤버 ID: {}, 티켓 타입: {}", memberId, ticketType);
        return ticketAccountService.hasTicket(memberId, ticketType);
    }

    /**
     * 티켓 사용
     */
    public TicketUsageResponse useTicket(Long memberId, TicketType ticketType) {
        log.info("티켓 사용 시작. 멤버 ID: {}, 티켓 타입: {}", memberId, ticketType);
        return ticketUsageService.useTicket(memberId, ticketType);
    }

    /**
     * 회원의 트랜잭션 내역 조회 (페이징)
     */
    public Page<TicketTransactionDto> getMemberTransactions(Long memberId, Pageable pageable) {
        return ticketTransactionService.getMemberTransactions(memberId, pageable);
    }

    /**
     * 특정 기간 동안의 회원 트랜잭션 내역 조회
     */
    public List<TicketTransactionDto> getMemberTransactionsBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        return ticketTransactionService.getMemberTransactionsBetween(memberId, start, end);
    }

    /**
     * 회원의 티켓 사용 내역 조회 (페이징)
     */
    public Page<TicketUsageDto> getMemberTicketUsages(Long memberId, Pageable pageable) {
        return ticketUsageService.getMemberTicketUsages(memberId, pageable);
    }

    /**
     * 특정 기간 동안의 회원 티켓 사용 내역 조회
     */
    public List<TicketUsageDto> getMemberTicketUsagesBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        return ticketUsageService.getMemberTicketUsagesBetween(memberId, start, end);
    }

    /**
     * 대기 중인 티켓 사용 내역 처리 (배치 작업)
     */
    public void processTicketUsageResults() {
        ticketUsageService.processTicketUsageResults();
    }

    /**
     * 티켓 사용 내역을 완료 상태로 변경
     */
    public void markTicketUsageAsCompleted(Long usageId) {
        log.info("티켓 사용 완료 처리 시작. 사용 ID: {}", usageId);
        ticketUsageService.markTicketUsageAsCompleted(usageId);
    }

    /**
     * 티켓 사용 내역을 실패 상태로 변경하고 티켓 환불 처리
     */
    public void markTicketUsageAsFailed(Long usageId) {
        log.info("티켓 사용 실패 처리 시작. 사용 ID: {}", usageId);
        ticketUsageService.markTicketUsageAsFailed(usageId);
    }

    /**
     * 특정 타입별 티켓 잔액 조회
     */
    public Long getTicketBalance(Long memberId, TicketType ticketType) {
        return ticketAccountService.getTicketBalance(memberId, ticketType);
    }

    /**
     * 티켓 계정 존재 여부 확인
     */
    public boolean hasTicketAccount(Long memberId) {
        return ticketAccountService.hasTicketAccount(memberId);
    }
}