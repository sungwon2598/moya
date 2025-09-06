package com.study.moya.admin.controller;

import com.study.moya.admin.dto.coupon.request.AdminCouponIssueRequest;
import com.study.moya.admin.service.AdminCouponService;
import com.study.moya.coupon.dto.CouponBatchRequest;
import com.study.moya.coupon.dto.CouponResponse;
import com.study.moya.coupon.exception.CouponErrorCode;
import com.study.moya.global.api.ApiResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/admin/coupons")
@Tag(name = "Admin Coupon", description = "관리자 쿠폰 관리 API")
public class AdminCouponController {

    private final AdminCouponService adminCouponService;
    private final SecurityHeadersConfig securityHeadersConfig;

    @PostMapping("/issue-to-member")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "특정 사용자에게 쿠폰 발급", description = "관리자가 특정 사용자에게 직접 쿠폰을 발급합니다.")
    public ResponseEntity<ApiResponse<String>> issueCouponToMember(
            @RequestBody AdminCouponIssueRequest request) {

        adminCouponService.issueCouponToMember( // ← 변경
                request.getTargetMemberId(),
                request.getCouponType(),
                request.getExpirationDate(),
                request.getBalance(),
                request.getReason()
        );

        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.status(HttpStatus.CREATED)
                        .body(ApiResponse.of("쿠폰 발급이 완료되었습니다"))
        );
    }

    /**
     * 쿠폰 생성
     */
    @PostMapping("/batch")
    @Operation(summary = "쿠폰 일괄 생성", description = "관리자가 쿠폰을 일괄 생성합니다. 생성된 쿠폰은 발급 대기 상태가 됩니다.")
    @SwaggerSuccessResponse(
            name = "쿠폰 생성이 완료됐습니다",
            value = CouponResponse.class,
            status = 201
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "잘못된 유효기간",
                    description = "유효기간이 현재 시간보다 이전",
                    value = CouponErrorCode.class,
                    code = "INVALID_EXPIRATION_DATE"
            )
    })
    public ResponseEntity<ApiResponse<CouponResponse>> createCoupons(
            @RequestBody CouponBatchRequest request) {
        CouponResponse responses = adminCouponService.createCoupons( // ← 변경
                request.getCount(),
                request.getCouponType(),
                request.getExpirationDate(),
                request.getBalance()
        );
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(responses)));
    }
}
