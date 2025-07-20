package com.study.moya.admin.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.ticket.TicketAccount;
import com.study.moya.token.domain.ticket.TicketTransaction;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.domain.enums.TransactionType;
import com.study.moya.token.dto.ticket.TicketAccountDto;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import com.study.moya.token.exception.ticket.TicketErrorCode;
import com.study.moya.token.exception.ticket.TicketException;
import com.study.moya.token.repository.ticket.TicketAccountRepository;
import com.study.moya.token.repository.ticket.TicketTransactionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminTicketService {

    private final TicketAccountRepository ticketAccountRepository;
    private final TicketTransactionRepository ticketTransactionRepository;
    private final MemberRepository memberRepository;

    /**
     * 특정 사용자에게 티켓 지급
     */
    @Transactional
    public void giveTicketsToMember(Long memberId, Long amount, TicketType ticketType, String reason) {
        log.info("티켓 지급 시작. 사용자 ID: {}, 티켓 타입: {}, 수량: {}, 사유: {}",
                memberId, ticketType, amount, reason);

        // 1. 사용자 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TicketException.of(TicketErrorCode.MEMBER_NOT_FOUND));

        // 2. 티켓 계정 조회 또는 생성
        TicketAccount ticketAccount = ticketAccountRepository.findByMemberId(memberId)
                .orElseGet(() -> createTicketAccount(member));

        // 3. 티켓 추가
        ticketAccount.addTicket(ticketType, amount);
        ticketAccountRepository.save(ticketAccount);

        // 4. 거래 내역 기록
        TicketTransaction transaction = TicketTransaction.builder()
                .ticketAccount(ticketAccount)
                .amount(amount)
                .transactionType(TransactionType.ADMIN_CHARGE)
                .ticketType(ticketType)
                .balanceAfter(getBalanceAfter(ticketAccount, ticketType))
                .description("관리자 지급: " + reason)
                .build();

        ticketTransactionRepository.save(transaction);

        log.info("티켓 지급 완료. 사용자 ID: {}, 현재 잔액: {}",
                memberId, getBalanceAfter(ticketAccount, ticketType));
    }

    /**
     * 특정 사용자에게서 티켓 회수
     */
    @Transactional
    public void deductTicketsFromMember(Long memberId, Long amount, TicketType ticketType, String reason) {
        log.info("티켓 회수 시작. 사용자 ID: {}, 티켓 타입: {}, 수량: {}, 사유: {}",
                memberId, ticketType, amount, reason);

        // 1. 티켓 계정 조회
        TicketAccount ticketAccount = getTicketAccount(memberId);

        // 2. 잔액 확인
        Long currentBalance = getBalanceAfter(ticketAccount, ticketType);
        if (currentBalance < amount) {
            throw TicketException.of(TicketErrorCode.INSUFFICIENT_TICKET);
        }

        // 3. 티켓 차감
        switch (ticketType) {
            case ROADMAP_TICKET -> ticketAccount.addTicket(ticketType, -amount);
            case WORKSHEET_TICKET -> ticketAccount.addTicket(ticketType, -amount);
        }
        ticketAccountRepository.save(ticketAccount);

        // 4. 거래 내역 기록
        TicketTransaction transaction = TicketTransaction.builder()
                .ticketAccount(ticketAccount)
                .amount(amount)
                .transactionType(TransactionType.ADMIN_DEDUCT)
                .ticketType(ticketType)
                .balanceAfter(getBalanceAfter(ticketAccount, ticketType))
                .description("관리자 회수: " + reason)
                .build();

        ticketTransactionRepository.save(transaction);

        log.info("티켓 회수 완료. 사용자 ID: {}, 현재 잔액: {}",
                memberId, getBalanceAfter(ticketAccount, ticketType));
    }

    /**
     * 전체 티켓 계정 조회 (페이징)
     */
    @Transactional(readOnly = true)
    public Page<TicketAccountDto> getAllTicketAccounts(Pageable pageable) {
        log.info("전체 티켓 계정 조회. 페이지: {}", pageable.getPageNumber());

        return ticketAccountRepository.findAll(pageable)
                .map(TicketAccountDto::from);
    }

    /**
     * 특정 사용자의 티켓 계정 조회
     */
    @Transactional(readOnly = true)
    public TicketAccountDto getTicketAccountByMemberId(Long memberId) {
        log.info("사용자 티켓 계정 조회. 사용자 ID: {}", memberId);

        TicketAccount ticketAccount = getTicketAccount(memberId);
        return TicketAccountDto.from(ticketAccount);
    }

    /**
     * 특정 사용자의 티켓 거래 내역 조회
     */
    @Transactional(readOnly = true)
    public List<TicketTransactionDto> getTicketTransactionsByMemberId(Long memberId, int limit) {
        TicketAccount ticketAccount = getTicketAccount(memberId);

        return ticketTransactionRepository.findByTicketAccountIdWithUsageOrderByCreatedAtDesc(ticketAccount.getId())  // 페치 조인 버전 사용
                .stream()
                .limit(limit)
                .map(TicketTransactionDto::from)
                .collect(Collectors.toList());
    }

    /**
     * 티켓 계정 생성
     */
    private TicketAccount createTicketAccount(Member member) {
        log.info("새 티켓 계정 생성. 사용자 ID: {}", member.getId());

        TicketAccount ticketAccount = TicketAccount.builder()
                .member(member)
                .roadmapTicketBalance(0L)
                .worksheetTicketBalance(0L)
                .build();

        return ticketAccountRepository.save(ticketAccount);
    }

    /**
     * 티켓 계정 조회 (없으면 예외)
     */
    private TicketAccount getTicketAccount(Long memberId) {
        return ticketAccountRepository.findByMemberIdWithMember(memberId)  // 이것만 변경
                .orElseThrow(() -> TicketException.of(TicketErrorCode.TICKET_ACCOUNT_NOT_FOUND));
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