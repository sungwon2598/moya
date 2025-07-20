package com.study.moya.admin.controller;

import com.study.moya.admin.dto.ticket.request.DeductTicketRequest;
import com.study.moya.admin.dto.ticket.request.GiveTicketRequest;
import com.study.moya.admin.service.AdminTicketService;
import com.study.moya.token.dto.ticket.TicketAccountDto;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/ticket")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final AdminTicketService adminTicketService;

    /**
     * 티켓 지급
     */
    @PostMapping("/give")
    public ResponseEntity<String> giveTickets(@RequestBody GiveTicketRequest request) {
        log.info("티켓 지급 요청. 사용자 ID: {}, 티켓 타입: {}, 수량: {}",
                request.getMemberId(), request.getTicketType(), request.getAmount());

        try {
            adminTicketService.giveTicketsToMember(
                    request.getMemberId(),
                    request.getAmount(),
                    request.getTicketType(),
                    request.getReason()
            );
            return ResponseEntity.ok("티켓 지급 완료");
        } catch (Exception e) {
            log.error("티켓 지급 실패", e);
            return ResponseEntity.badRequest().body("티켓 지급 실패: " + e.getMessage());
        }
    }

    /**
     * 티켓 회수
     */
    @PostMapping("/deduct")
    public ResponseEntity<String> deductTickets(@RequestBody DeductTicketRequest request) {
        log.info("티켓 회수 요청. 사용자 ID: {}, 티켓 타입: {}, 수량: {}",
                request.getMemberId(), request.getTicketType(), request.getAmount());

        try {
            adminTicketService.deductTicketsFromMember(
                    request.getMemberId(),
                    request.getAmount(),
                    request.getTicketType(),
                    request.getReason()
            );
            return ResponseEntity.ok("티켓 회수 완료");
        } catch (Exception e) {
            log.error("티켓 회수 실패", e);
            return ResponseEntity.badRequest().body("티켓 회수 실패: " + e.getMessage());
        }
    }

    /**
     * 특정 사용자의 티켓 현황 조회
     */
    @GetMapping("/account/{memberId}")
    public ResponseEntity<TicketAccountDto> getTicketAccount(@PathVariable Long memberId) {
        log.info("사용자 티켓 현황 조회. 사용자 ID: {}", memberId);

        try {
            TicketAccountDto ticketAccount = adminTicketService.getTicketAccountByMemberId(memberId);
            return ResponseEntity.ok(ticketAccount);
        } catch (Exception e) {
            log.error("티켓 현황 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 전체 티켓 현황 조회 (페이징)
     */
    @GetMapping("/accounts")
    public ResponseEntity<Page<TicketAccountDto>> getAllTicketAccounts(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        log.info("전체 티켓 현황 조회. 페이지: {}, 사이즈: {}", page, size);

        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<TicketAccountDto> ticketAccounts = adminTicketService.getAllTicketAccounts(pageable);
            return ResponseEntity.ok(ticketAccounts);
        } catch (Exception e) {
            log.error("전체 티켓 현황 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 특정 사용자의 티켓 거래 내역 조회
     */
    @GetMapping("/transactions/{memberId}")
    public ResponseEntity<List<TicketTransactionDto>> getTicketTransactions(
            @PathVariable Long memberId,
            @RequestParam(defaultValue = "10") int limit) {
        log.info("사용자 티켓 거래 내역 조회. 사용자 ID: {}, 제한: {}", memberId, limit);

        try {
            List<TicketTransactionDto> transactions = adminTicketService.getTicketTransactionsByMemberId(memberId, limit);
            return ResponseEntity.ok(transactions);
        } catch (Exception e) {
            log.error("티켓 거래 내역 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }
}