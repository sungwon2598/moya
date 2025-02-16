package com.study.moya.coupon.controller;

import com.study.moya.coupon.dto.CouponResponse;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import com.study.moya.coupon.dto.CouponBatchRequest;
import com.study.moya.coupon.dto.CouponIssueRequest;
import com.study.moya.coupon.dto.CouponUseRequest;
import com.study.moya.coupon.exception.CouponErrorCode;
import com.study.moya.coupon.service.CouponService;
import com.study.moya.global.api.ApiResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/coupons")
@Tag(name = "Coupon", description = "쿠폰 관련 API")
public class CouponController {

    private final CouponService couponService;
    private final SecurityHeadersConfig securityHeadersConfig;


    /**
     * 쿠폰 발급
     */
    @Operation(summary = "쿠폰 발급", description = "회원에게 쿠폰을 발급합니다.")
    @SwaggerSuccessResponse(status = 201, name = "쿠폰 발급이 완료됐습니다")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "발급 가능한 쿠폰 없음", value = CouponErrorCode.class, code = "NO_COUPON_AVAILABLE"),
            @SwaggerErrorDescription(name = "회원 찾을 수 없음", value = CouponErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "쿠폰 발급 실패", description = "최대 재시도 횟수 초과", value = CouponErrorCode.class, code = "ISSUE_FAILED"),
            @SwaggerErrorDescription(name = "이미 발급된 쿠폰", value = CouponErrorCode.class, code = "ALREADY_USED_COUPON"),
            @SwaggerErrorDescription(name = "쿠폰 발급 처리 중 오류", description = "동시성 처리 중 발생한 오류", value = CouponErrorCode.class, code = "COUPON_ISSUE_LOCK_ACQUISITION_FAILED")
    })
    @PostMapping
    public ResponseEntity<ApiResponse<String>> issueCoupon(
            @AuthenticationPrincipal Long memberId,
            @RequestBody CouponIssueRequest request) {
        couponService.issueCoupon(memberId, request.getCouponType());
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("쿠폰 발급이 완료됐습니다")));
    }

    /**
     * 쿠폰 사용
     */
    @Operation(summary = "쿠폰 사용", description = "회원의 보유 쿠폰을 사용 처리합니다. 쿠폰 사용 후 취소는 불가능합니다.")
    @SwaggerSuccessResponse(
            name = "쿠폰 사용이 완료됐습니다"
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "쿠폰 없음",
                    value = CouponErrorCode.class,
                    code = "COUPON_NOT_FOUND"
            ),
            @SwaggerErrorDescription(
                    name = "만료된 쿠폰",
                    value = CouponErrorCode.class,
                    code = "EXPIRED_COUPON"
            ),
            @SwaggerErrorDescription(
                    name = "이미 사용된 쿠폰",
                    value = CouponErrorCode.class,
                    code = "ALREADY_USED_COUPON"
            ),
            @SwaggerErrorDescription(
                    name = "쿠폰 사용 처리 중 오류",
                    description = "동시성 처리 중 발생한 오류",
                    value = CouponErrorCode.class,
                    code = "COUPON_LOCK_ACQUISITION_FAILED"
            )
    })
    @PatchMapping("/use")
    public ResponseEntity<ApiResponse<String>> useCoupon(
            @AuthenticationPrincipal Long memberId,
            @RequestBody CouponUseRequest request
            ) {
        couponService.useCoupon(memberId, request.getCouponType());
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of("쿠폰 사용이 완료됐습니다")));
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
        CouponResponse responses = couponService.createCoupons(request.getCount(), request.getCouponType(), request.getExpirationDate());
        return securityHeadersConfig.addSecurityHeaders(ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.of(responses)));
    }
}