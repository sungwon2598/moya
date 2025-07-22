package com.study.moya.admin.controller;

import com.study.moya.admin.dto.ticket.request.DeductTicketRequest;
import com.study.moya.admin.dto.ticket.request.GiveTicketRequest;
import com.study.moya.admin.exception.AdminErrorCode;
import com.study.moya.admin.service.AdminTicketService;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import com.study.moya.token.dto.ticket.TicketAccountDto;
import com.study.moya.token.dto.ticket.TicketTransactionDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Ticket", description = "관리자 티켓 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/ticket")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminTicketController {

    private final AdminTicketService adminTicketService;

    @Operation(summary = "티켓 지급", description = "특정 회원에게 티켓을 지급합니다")
    @SwaggerSuccessResponse(status = 200, name = "티켓 지급 성공", value = String.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "회원 없음", description = "해당 회원을 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "처리 실패", description = "티켓 지급 처리 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PostMapping("/give")
    public ResponseEntity<String> giveTickets(
            @Parameter(description = "티켓 지급 요청 정보")
            @RequestBody GiveTicketRequest request) {
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

    @Operation(summary = "티켓 회수", description = "특정 회원의 티켓을 회수합니다")
    @SwaggerSuccessResponse(status = 200, name = "티켓 회수 성공", value = String.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "회원 없음", description = "해당 회원을 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "처리 실패", description = "티켓 회수 처리 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PostMapping("/deduct")
    public ResponseEntity<String> deductTickets(
            @Parameter(description = "티켓 회수 요청 정보")
            @RequestBody DeductTicketRequest request) {
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

    @Operation(summary = "특정 회원 티켓 현황 조회", description = "특정 회원의 티켓 보유 현황을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "티켓 현황 조회 성공", value = TicketAccountDto.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "회원 없음", description = "해당 회원을 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "조회 실패", description = "티켓 현황 조회 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @GetMapping("/account/{memberId}")
    public ResponseEntity<TicketAccountDto> getTicketAccount(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId) {
        log.info("사용자 티켓 현황 조회. 사용자 ID: {}", memberId);

        try {
            TicketAccountDto ticketAccount = adminTicketService.getTicketAccountByMemberId(memberId);
            return ResponseEntity.ok(ticketAccount);
        } catch (Exception e) {
            log.error("티켓 현황 조회 실패", e);
            return ResponseEntity.badRequest().build();
        }
    }

    @Operation(summary = "전체 회원 티켓 현황 조회", description = "모든 회원의 티켓 보유 현황을 페이징으로 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "전체 티켓 현황 조회 성공", value = Page.class)
    @SwaggerErrorDescription(name = "조회 실패", description = "티켓 현황 조회 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    @GetMapping("/accounts")
    public ResponseEntity<Page<TicketAccountDto>> getAllTicketAccounts(
            @Parameter(description = "페이지 번호 (0부터 시작)", example = "0")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "페이지당 항목 수", example = "20")
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

    @Operation(summary = "회원 티켓 거래 내역 조회", description = "특정 회원의 티켓 거래 내역을 최신순으로 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "티켓 거래 내역 조회 성공", value = List.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "회원 없음", description = "해당 회원을 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "조회 실패", description = "거래 내역 조회 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @GetMapping("/transactions/{memberId}")
    public ResponseEntity<List<TicketTransactionDto>> getTicketTransactions(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId,
            @Parameter(description = "조회할 거래 내역 개수", example = "10")
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