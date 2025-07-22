package com.study.moya.token.service.ticket;

import com.study.moya.token.domain.ticket.TicketAccount;
import com.study.moya.token.domain.ticket.TicketUsage;
import com.study.moya.token.domain.ticket.TicketTransaction;
import com.study.moya.token.domain.enums.AiServiceType;
import com.study.moya.token.domain.enums.AiUsageStatus;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.enums.TransactionType;
import com.study.moya.token.dto.ticket.TicketUsageDto;
import com.study.moya.token.dto.ticket.reponse.TicketUsageResponse;
import com.study.moya.token.exception.ticket.TicketErrorCode;
import com.study.moya.token.exception.ticket.TicketException;
import com.study.moya.token.repository.ticket.TicketUsageRepository;
import com.study.moya.token.repository.ticket.TicketTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketUsageService {

    private final TicketUsageRepository ticketUsageRepository;
    private final TicketTransactionRepository ticketTransactionRepository;
    private final TicketAccountService ticketAccountService;

    /**
     * 티켓 사용
     */
    @Transactional
    public TicketUsageResponse useTicket(Long memberId, TicketType ticketType) {
        log.info("티켓 사용. 멤버 ID: {}, 티켓 타입: {}", memberId, ticketType);

        // 1. 티켓 계정 조회
        TicketAccount ticketAccount = ticketAccountService.getTicketAccount(memberId);

        // 2. 티켓 보유 확인 및 사용
        ticketAccount.useTicket(ticketType);

        // 3. 사용 내역 생성
        TicketUsage ticketUsage = TicketUsage.builder()
                .member(ticketAccount.getMember())
                .serviceType(getServiceType(ticketType))
                .ticketType(ticketType)
                .status(AiUsageStatus.PENDING)
                .build();

        TicketUsage savedUsage = ticketUsageRepository.save(ticketUsage);

        // 4. 거래 내역 기록
        TicketTransaction transaction = TicketTransaction.builder()
                .ticketAccount(ticketAccount)
                .ticketUsage(savedUsage)
                .amount(1L)
                .transactionType(TransactionType.USAGE)
                .ticketType(ticketType)
                .balanceAfter(getBalanceAfter(ticketAccount, ticketType))
                .description(ticketType + " 사용")
                .build();

        ticketTransactionRepository.save(transaction);

        return new TicketUsageResponse(savedUsage.getId());
    }

    /**
     * 회원의 티켓 사용 내역 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<TicketUsageDto> getMemberTicketUsages(Long memberId, Pageable pageable) {
        log.info("회원 티켓 사용 내역 조회. 멤버 ID: {}, 페이지: {}", memberId, pageable.getPageNumber());

        return ticketUsageRepository.findByMemberIdOrderByCreatedAtDesc(memberId, pageable)
                .map(TicketUsageDto::from);
    }

    /**
     * 특정 기간 동안의 회원 티켓 사용 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TicketUsageDto> getMemberTicketUsagesBetween(Long memberId, LocalDateTime start, LocalDateTime end) {
        log.info("기간별 회원 티켓 사용 내역 조회. 멤버 ID: {}, 기간: {} ~ {}", memberId, start, end);

        return ticketUsageRepository.findByMemberIdAndCreatedAtBetweenOrderByCreatedAtDesc(memberId, start, end)
                .stream()
                .map(TicketUsageDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 대기 중인 티켓 사용 내역 처리 (배치 작업)
     */
    @Transactional
    public void processTicketUsageResults() {
        log.info("대기 중인 티켓 사용 내역 처리 시작");

        List<TicketUsage> pendingUsages = ticketUsageRepository.findByStatus(AiUsageStatus.PENDING);

        // 실제 배치 처리 로직은 별도 구현 필요
        log.info("처리할 대기 중인 티켓 사용 내역: {} 건", pendingUsages.size());
    }

    /**
     * 티켓 사용 내역을 완료 상태로 변경
     */
    @Transactional
    public void markTicketUsageAsCompleted(Long usageId) {
        log.info("티켓 사용 완료 처리. 사용 ID: {}", usageId);

        TicketUsage ticketUsage = ticketUsageRepository.findById(usageId)
                .orElseThrow(() -> TicketException.of(TicketErrorCode.TICKET_USAGE_NOT_FOUND));

        if (ticketUsage.getStatus() == AiUsageStatus.COMPLETED) {
            throw TicketException.of(TicketErrorCode.TICKET_USAGE_ALREADY_COMPLETED);
        }

        ticketUsage.completeUsage();
        ticketUsageRepository.save(ticketUsage);
    }

    /**
     * 티켓 사용 내역을 실패 상태로 변경하고 티켓 환불 처리
     */
    @Transactional
    public void markTicketUsageAsFailed(Long usageId) {
        log.info("티켓 사용 실패 처리. 사용 ID: {}", usageId);

        TicketUsage ticketUsage = ticketUsageRepository.findById(usageId)
                .orElseThrow(() -> TicketException.of(TicketErrorCode.TICKET_USAGE_NOT_FOUND));

        if (ticketUsage.getStatus() == AiUsageStatus.FAILED) {
            throw TicketException.of(TicketErrorCode.TICKET_USAGE_ALREADY_FAILED);
        }

        ticketUsage.failUsage();

        // 티켓 환불
        TicketAccount ticketAccount = ticketAccountService.getTicketAccount(ticketUsage.getMember().getId());
        ticketAccount.addTicket(ticketUsage.getTicketType(), 1L);

        // 환불 거래 내역 기록
        TicketTransaction refundTransaction = TicketTransaction.builder()
                .ticketAccount(ticketAccount)
                .ticketUsage(ticketUsage)
                .amount(1L)
                .transactionType(TransactionType.REFUND)
                .ticketType(ticketUsage.getTicketType())
                .balanceAfter(getBalanceAfter(ticketAccount, ticketUsage.getTicketType()))
                .description(ticketUsage.getTicketType() + " 환불")
                .build();

        ticketTransactionRepository.save(refundTransaction);
    }

    /**
     * 티켓 타입에 따른 서비스 타입 반환
     */
    private AiServiceType getServiceType(TicketType ticketType) {
        return switch (ticketType) {
            case ROADMAP_TICKET -> AiServiceType.ROADMAP;
            case WORKSHEET_TICKET -> AiServiceType.WORKSHEET;
        };
    }

    /**
     * 거래 후 잔액 계산
     */
    private Long getBalanceAfter(TicketAccount account, TicketType ticketType) {
        return switch (ticketType) {
            case ROADMAP_TICKET -> account.getRoadmapTicketBalance();
            case WORKSHEET_TICKET -> account.getWorksheetTicketBalance();
        };
    }
}