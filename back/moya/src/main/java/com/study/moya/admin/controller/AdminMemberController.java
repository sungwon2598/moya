package com.study.moya.admin.controller;

import com.study.moya.admin.dto.member.AdminMemberDetailResponse;
import com.study.moya.admin.dto.member.AdminMemberResponse;
import com.study.moya.admin.service.AdminMemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/members")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;


    //테스트입니다1
    @GetMapping
    public Page<AdminMemberResponse> getMembers(Pageable pageable) {
        return adminMemberService.getMembers(pageable);
    }

    @GetMapping("/{memberId}")
    public AdminMemberDetailResponse getMemberDetail(@PathVariable Long memberId) {
        return adminMemberService.getMemberDetail(memberId);
    }

    @PatchMapping("/{memberId}/block")
    public void blockMember(@PathVariable Long memberId, @RequestBody String reason) {
        adminMemberService.blockMember(memberId, reason);
    }
}



