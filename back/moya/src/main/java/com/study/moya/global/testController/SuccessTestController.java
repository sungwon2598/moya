package com.study.moya.global.testController;

import com.study.moya.global.api.ApiResponse;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/test/success")
public class SuccessTestController {

    @GetMapping("/member")
    public ApiResponse<MemberResponse> getMember() {
        MemberResponse member = new MemberResponse(
                1L,
                "test@email.com",
                "테스터",
                "ROLE_USER",
                LocalDateTime.now()
        );
        return ApiResponse.of(member);
    }

    @GetMapping("/members")
    public ApiResponse<List<MemberResponse>> getMembers() {
        List<MemberResponse> members = List.of(
                new MemberResponse(1L, "user1@email.com", "유저1", "ROLE_USER", LocalDateTime.now()),
                new MemberResponse(2L, "user2@email.com", "유저2", "ROLE_USER", LocalDateTime.now()),
                new MemberResponse(3L, "admin@email.com", "관리자", "ROLE_ADMIN", LocalDateTime.now())
        );
        return ApiResponse.of(members);
    }

    @GetMapping("/members/page")
    public ApiResponse<List<MemberResponse>> getMemberPage(Pageable pageable) {
        List<MemberResponse> members = List.of(
                new MemberResponse(1L, "user1@email.com", "유저1", "ROLE_USER", LocalDateTime.now()),
                new MemberResponse(2L, "user2@email.com", "유저2", "ROLE_USER", LocalDateTime.now()),
                new MemberResponse(3L, "admin@email.com", "관리자", "ROLE_ADMIN", LocalDateTime.now())
        );

        Page<MemberResponse> memberPage = new PageImpl<>(
                members,
                pageable,
                100 // total elements
        );

        return ApiResponse.of(memberPage);
    }

    @PostMapping("/member")
    public ApiResponse<MemberResponse> createMember(@RequestBody MemberCreateRequest request) {
        MemberResponse member = new MemberResponse(
                1L,
                request.email(),
                request.name(),
                "ROLE_USER",
                LocalDateTime.now()
        );
        return ApiResponse.of(member);
    }

    @Getter
    @AllArgsConstructor
    static class MemberResponse {
        private Long id;
        private String email;
        private String name;
        private String role;
        private LocalDateTime createdAt;
    }

    record MemberCreateRequest(
            String email,
            String name,
            String password
    ) {}
}