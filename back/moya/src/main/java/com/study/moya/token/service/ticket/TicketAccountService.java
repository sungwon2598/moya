package com.study.moya.token.service.ticket;

import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.ticket.TicketAccount;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.dto.ticket.TicketAccountDto;
import com.study.moya.token.dto.ticket.reponse.TicketBalanceResponse;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import com.study.moya.token.exception.ticket.TicketErrorCode;
import com.study.moya.token.exception.ticket.TicketException;
import com.study.moya.token.repository.ticket.TicketAccountRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class TicketAccountService {

    private final TicketAccountRepository ticketAccountRepository;
    private final MemberRepository memberRepository;
    private final TicketTransactionService ticketTransactionService;

    /**
     * 티켓 계정 조회 또는 생성
     */
    @Transactional
    public TicketAccountDto getOrCreateTicketAccount(Long memberId) {
        log.info("티켓 계정 조회 또는 생성. 멤버 ID: {}", memberId);

        TicketAccount ticketAccount = ticketAccountRepository.findByMemberId(memberId)
                .orElseGet(() -> createTicketAccount(memberId));

        return TicketAccountDto.from(ticketAccount);
    }

    /**
     * 티켓 잔액 조회
     */
    @Transactional(readOnly = true)
    public TicketBalanceResponse getTicketBalance(Long memberId) {
        log.info("티켓 잔액 조회. 멤버 ID: {}", memberId);

        TicketAccount ticketAccount = getTicketAccount(memberId);
        List<TicketTransactionDto> recentTransactions = ticketTransactionService.getRecentTransactions(memberId, 5);

        return TicketBalanceResponse.from(ticketAccount, recentTransactions);
    }

    /**
     * 특정 타입 티켓 보유 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasTicket(Long memberId, TicketType ticketType) {
        return ticketAccountRepository.findByMemberId(memberId)
                .map(account -> account.hasTicket(ticketType))
                .orElse(false);
    }

    /**
     * 특정 타입 티켓 잔액 조회
     */
    @Transactional(readOnly = true)
    public Long getTicketBalance(Long memberId, TicketType ticketType) {
        TicketAccount ticketAccount = getTicketAccount(memberId);

        return switch (ticketType) {
            case ROADMAP_TICKET -> ticketAccount.getRoadmapTicketBalance();
            case WORKSHEET_TICKET -> ticketAccount.getWorksheetTicketBalance();
        };
    }

    /**
     * 티켓 계정 존재 여부 확인
     */
    @Transactional(readOnly = true)
    public boolean hasTicketAccount(Long memberId) {
        return ticketAccountRepository.existsByMemberId(memberId);
    }

    /**
     * 티켓 계정 생성
     */
    private TicketAccount createTicketAccount(Long memberId) {
        log.info("새 티켓 계정 생성. 멤버 ID: {}", memberId);

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> TicketException.of(TicketErrorCode.MEMBER_NOT_FOUND));

        TicketAccount ticketAccount = TicketAccount.builder()
                .member(member)
                .roadmapTicketBalance(10L)
                .worksheetTicketBalance(10L)
                .build();

        return ticketAccountRepository.save(ticketAccount);
    }

    /**
     * 티켓 계정 조회 (없으면 예외)
     */
    public TicketAccount getTicketAccount(Long memberId) {
        return ticketAccountRepository.findByMemberId(memberId)
                .orElseThrow(() -> TicketException.of(TicketErrorCode.TICKET_ACCOUNT_NOT_FOUND));
    }
}