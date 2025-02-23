package com.study.moya.mypage.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.global.config.security.SecurityHeadersConfig;
import com.study.moya.mypage.dto.MyPageResponse;
import com.study.moya.mypage.dto.MyPageUpdateRequest;
import com.study.moya.mypage.exception.MyPageErrorCode;
import com.study.moya.mypage.service.MyPageService;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/mypage")
public class MyPageController {
    private final MyPageService myPageService;
    private final SecurityHeadersConfig securityHeadersConfig;

    @Operation(summary = "마이페이지 조회", description = "사용자의 마이페이지 정보를 조회합니다.")
    @SwaggerSuccessResponse(
            status = 200,
            name = "마이페이지 조회가 완료됐습니다",
            value = MyPageResponse.class
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "회원을 찾을 수 없음",
                    value = MyPageErrorCode.class,
                    code = "MEMBER_NOT_FOUND"
            ),
            @SwaggerErrorDescription(
                    name = "차단된 회원",
                    value = MyPageErrorCode.class,
                    code = "MEMBER_BLOCKED"
            ),
            @SwaggerErrorDescription(
                    name = "탈퇴한 회원",
                    value = MyPageErrorCode.class,
                    code = "MEMBER_WITHDRAWN"
            )
    })
    @GetMapping
    public ResponseEntity<ApiResponse<MyPageResponse>> getMyPage(@AuthenticationPrincipal Long id) {
        MyPageResponse response = myPageService.getMyPageInfo(id);
        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.ok(ApiResponse.of(response)));
    }

    @Operation(summary = "마이페이지 수정", description = "사용자의 마이페이지 정보를 수정합니다.")
    @SwaggerSuccessResponse(
            status = 200,
            name = "마이페이지 수정이 완료됐습니다",
            value = MyPageResponse.class
    )
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(
                    name = "회원을 찾을 수 없음",
                    value = MyPageErrorCode.class,
                    code = "MEMBER_NOT_FOUND"
            ),
            @SwaggerErrorDescription(
                    name = "회원 정보 수정 불가",
                    value = MyPageErrorCode.class,
                    code = "MEMBER_NOT_MODIFIABLE"
            ),
            @SwaggerErrorDescription(
                    name = "중복된 닉네임",
                    value = MyPageErrorCode.class,
                    code = "DUPLICATE_NICKNAME"
            )
    })
    @PutMapping
    public ResponseEntity<ApiResponse<MyPageResponse>> updateMyPage(
            @AuthenticationPrincipal Long id,
            @Valid @RequestBody MyPageUpdateRequest request) {
        MyPageResponse response = myPageService.updateMyPage(id, request);
        return securityHeadersConfig.addSecurityHeaders(
                ResponseEntity.ok(ApiResponse.of(response)));    }
}
