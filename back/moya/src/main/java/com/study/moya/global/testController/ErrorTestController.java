package com.study.moya.global.testController;

import com.study.moya.error.constants.AuthErrorCode;
import com.study.moya.error.constants.MemberErrorCode;
import com.study.moya.error.exception.AuthException;
import com.study.moya.error.exception.MemberException;
import com.study.moya.global.api.ApiResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/test/error")
public class ErrorTestController {

    @GetMapping("/member-not-found")
    public ApiResponse<Void> memberNotFoundException() {
        throw MemberException.of(MemberErrorCode.MEMBER_NOT_FOUND);
    }

    @GetMapping("/member-suspended")
    public ApiResponse<Void> memberSuspendedException() {
        throw MemberException.of(MemberErrorCode.MEMBER_SUSPENDED);
    }

    @GetMapping("/auth-unauthorized")
    public ApiResponse<Void> authUnauthorizedException() {
        throw AuthException.of(AuthErrorCode.UNAUTHORIZED);
    }

//    @PreAuthorize("hasRole('ADMIN')")
//    @GetMapping("/auth-access-denied")
//    public ApiResponse<Void> accessDeniedException() {
//        return ApiResponse.of(null);
//    }
//
//    @GetMapping("/type-mismatch")
//    public ApiResponse<Void> typeMismatch(@RequestParam Long id) {
//        return ApiResponse.of(null);
//    }
//
//    @PostMapping("/validation")
//    public ApiResponse<Void> validation(@Valid @RequestBody TestRequest request) {
//        return ApiResponse.of(null);
//    }

    @Getter
    @RequiredArgsConstructor
    public static class TestRequest {
        @NotBlank(message = "이름은 필수입니다")
        private final String name;

        @Email(message = "이메일 형식이 올바르지 않습니다")
        @NotBlank(message = "이메일은 필수입니다")
        private final String email;
    }
}