package com.study.moya.token.controller;

import com.study.moya.token.dto.account.TokenBalanceResponse;
import com.study.moya.token.dto.charge.ChargeTokenRequest;
import com.study.moya.token.dto.charge.PaymentConfirmRequest;
import com.study.moya.token.dto.charge.PaymentResponse;
import com.study.moya.token.dto.charge.TokenPackageResponse;
import com.study.moya.token.dto.transaction.TokenTransactionDto;
import com.study.moya.token.dto.usage.AiServiceResponse;
import com.study.moya.token.dto.usage.AiUsageDto;
import com.study.moya.token.dto.usage.AiUsageResponse;
import com.study.moya.token.dto.usage.UseTokenRequest;
import com.study.moya.token.service.TokenFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/tokens")
@RequiredArgsConstructor
public class TokenApiController {

    private final TokenFacadeService tokenFacadeService;

    @GetMapping("/balance")
    public ResponseEntity<TokenBalanceResponse> getTokenBalance(
            @AuthenticationPrincipal Long memberId) {

        return ResponseEntity.ok(tokenFacadeService.getTokenBalance(memberId));
    }

    @GetMapping("/packages")
    public ResponseEntity<TokenPackageResponse> getAvailableTokenPackages() {
        return ResponseEntity.ok(tokenFacadeService.getAvailableTokenPackages());
    }

    @PostMapping("/charge")
    public ResponseEntity<PaymentResponse> requestTokenCharge(
            @AuthenticationPrincipal Long memberId,
            @RequestBody ChargeTokenRequest request) {

        return ResponseEntity.ok(tokenFacadeService.requestTokenCharge(memberId, request));
    }

    @PostMapping("/payments/confirm")
    public ResponseEntity<PaymentResponse> confirmPayment(
            @RequestBody PaymentConfirmRequest request) {

        return ResponseEntity.ok(tokenFacadeService.confirmPayment(request));
    }

    @GetMapping("/ai-services")
    public ResponseEntity<AiServiceResponse> getAvailableAiServices() {
        return ResponseEntity.ok(tokenFacadeService.getAvailableAiServices());
    }

    @GetMapping("/ai-services/type/{serviceType}")
    public ResponseEntity<AiServiceResponse> getAvailableAiServicesByType(
            @PathVariable String serviceType) {

        return ResponseEntity.ok(tokenFacadeService.getAvailableAiServicesByType(serviceType));
    }

    @PostMapping("/use")
    public ResponseEntity<AiUsageResponse> useTokenForAiService(
            @AuthenticationPrincipal Long memberId,
            @RequestBody UseTokenRequest request) {;

        return ResponseEntity.ok(tokenFacadeService.useTokenForAiService(memberId, request));
    }

    @GetMapping("/transactions")
    public ResponseEntity<Page<TokenTransactionDto>> getMemberTransactions(
            @AuthenticationPrincipal Long memberId,
            Pageable pageable) {

        return ResponseEntity.ok(tokenFacadeService.getMemberTransactions(memberId, pageable));
    }

    @GetMapping("/transactions/period")
    public ResponseEntity<List<TokenTransactionDto>> getMemberTransactionsBetween(
            @AuthenticationPrincipal Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {

        return ResponseEntity.ok(tokenFacadeService.getMemberTransactionsBetween(memberId, start, end));
    }

    @GetMapping("/ai-usages")
    public ResponseEntity<Page<AiUsageDto>> getMemberAiUsages(
            @AuthenticationPrincipal Long memberId,
            Pageable pageable) {


        return ResponseEntity.ok(tokenFacadeService.getMemberAiUsages(memberId, pageable));
    }

    @GetMapping("/ai-usages/period")
    public ResponseEntity<List<AiUsageDto>> getMemberAiUsagesBetween(
            @AuthenticationPrincipal Long memberId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end) {


        return ResponseEntity.ok(tokenFacadeService.getMemberAiUsagesBetween(memberId, start, end));
    }

    // 관리자 전용 API
    @PostMapping("/admin/process-ai-usages")
    public ResponseEntity<Void> processAiUsageResults() {
        tokenFacadeService.processAiUsageResults();
        return ResponseEntity.ok().build();
    }

}