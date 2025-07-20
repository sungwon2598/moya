package com.study.moya.admin.controller;

import com.study.moya.admin.dto.member.AdminMemberDetailResponse;
import com.study.moya.admin.dto.member.AdminMemberResponse;
import com.study.moya.admin.service.AdminMemberService;
import com.study.moya.member.domain.MemberStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/members")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;


    //테스트입니다2
    @GetMapping
    public Page<AdminMemberResponse> getMembers(Pageable pageable) {
        return adminMemberService.getMembers(pageable);
    }

    /**
     * 상태별 멤버 조회
     */
    @GetMapping("/status/{status}")
    public Page<AdminMemberResponse> getMembersByStatus(
            @PathVariable MemberStatus status,
            Pageable pageable) {
        return adminMemberService.getMembersByStatus(status, pageable);
    }

    /**
     * 전체 상태별 멤버 개수 조회
     */
    @GetMapping("/status/counts")
    public Map<String, Long> getAllMemberCountsByStatus() {
        return adminMemberService.getAllMemberCountsByStatus();
    }

    @GetMapping("/{memberId}")
    public AdminMemberDetailResponse getMemberDetail(@PathVariable Long memberId) {
        return adminMemberService.getMemberDetail(memberId);
    }

    @PatchMapping("/{memberId}/block")
    public void blockMember(@PathVariable Long memberId, @RequestBody String reason) {
        adminMemberService.blockMember(memberId, reason);
    }

    @DeleteMapping("/{memberId}/withdraw")
    public void withdrawMember(@PathVariable Long memberId) {
        adminMemberService.withdrawMember(memberId);
    }

    @PatchMapping("/{memberId}/dormant")
    public void setMemberDormant(@PathVariable Long memberId) {
        adminMemberService.dormantMember(memberId);
    }

    @PatchMapping("/{memberId}/unblock")
    public void unblockMember(@PathVariable Long memberId) {
        adminMemberService.unblockMember(memberId);
    }
}



