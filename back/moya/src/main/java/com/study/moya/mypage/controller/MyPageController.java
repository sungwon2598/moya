package com.study.moya.mypage.controller;

import com.study.moya.global.api.ApiResponse;
import com.study.moya.mypage.dto.MyPageResponse;
import com.study.moya.mypage.dto.MyPageUpdateRequest;
import com.study.moya.mypage.service.MyPageService;
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

    @GetMapping
    public ResponseEntity<MyPageResponse> getMyPage(@AuthenticationPrincipal String email) {
        MyPageResponse response = myPageService.getMyPageInfo(email);
        return ResponseEntity.ok(response);
    }

    @PutMapping
    public ResponseEntity<MyPageResponse> updateMyPage(
            @AuthenticationPrincipal String email,
            @Valid @RequestBody MyPageUpdateRequest request) {
        MyPageResponse response = myPageService.updateMyPage(email, request);
        return ResponseEntity.ok(response);
    }
}
