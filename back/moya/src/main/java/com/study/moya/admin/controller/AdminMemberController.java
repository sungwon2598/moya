package com.study.moya.admin.controller;

import com.study.moya.admin.dto.member.AdminMemberDetailResponse;
import com.study.moya.admin.dto.member.AdminMemberResponse;
import com.study.moya.admin.exception.AdminErrorCode;
import com.study.moya.admin.service.AdminMemberService;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@Tag(name = "Admin Member", description = "관리자 멤버 관리 API")
@RestController
@RequiredArgsConstructor
@RequestMapping("api/admin/members")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminMemberController {
    private final AdminMemberService adminMemberService;

    @Operation(summary = "전체 멤버 조회", description = "전체 멤버 목록을 페이징으로 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 목록 조회 성공", value = Page.class)
    @GetMapping
    public Page<AdminMemberResponse> getMembers(
            @Parameter(description = "페이징 정보") Pageable pageable) {
        return adminMemberService.getMembers(pageable);
    }

    @Operation(summary = "상태별 멤버 조회", description = "특정 상태의 멤버 목록을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "상태별 멤버 조회 성공", value = Page.class)
    @GetMapping("/status/{status}")
    public Page<AdminMemberResponse> getMembersByStatus(
            @Parameter(description = "멤버 상태 (ACTIVE, BLOCKED, DORMANT, WITHDRAWN)", example = "ACTIVE")
            @PathVariable MemberStatus status,
            @Parameter(description = "페이징 정보")
            Pageable pageable) {
        return adminMemberService.getMembersByStatus(status, pageable);
    }

    @Operation(summary = "전체 상태별 멤버 개수 조회", description = "모든 상태별 멤버 개수를 한 번에 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "상태별 개수 조회 성공", value = Map.class)
    @GetMapping("/status/counts")
    public Map<String, Long> getAllMemberCountsByStatus() {
        return adminMemberService.getAllMemberCountsByStatus();
    }

    @Operation(summary = "멤버 상세 정보 조회", description = "특정 멤버의 상세 정보를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 상세 조회 성공", value = AdminMemberDetailResponse.class)
    @SwaggerErrorDescription(name = "멤버 없음", description = "해당 멤버를 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND")
    @GetMapping("/{memberId}")
    public AdminMemberDetailResponse getMemberDetail(
            @Parameter(description = "멤버 ID", example = "1")
            @PathVariable Long memberId) {
        return adminMemberService.getMemberDetail(memberId);
    }

    @Operation(summary = "멤버 차단", description = "특정 멤버를 차단 상태로 변경합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 차단 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "멤버 없음", description = "해당 멤버를 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "이미 차단됨", description = "이미 차단된 멤버입니다", value = AdminErrorCode.class, code = "ALREADY_BLOCKED_MEMBER"),
            @SwaggerErrorDescription(name = "차단 사유 누락", description = "차단 사유를 입력해주세요", value = AdminErrorCode.class, code = "BLOCK_REASON_REQUIRED")
    })
    @PatchMapping("/{memberId}/block")
    public void blockMember(
            @Parameter(description = "멤버 ID", example = "1")
            @PathVariable Long memberId,
            @Parameter(description = "차단 사유", example = "부적절한 행동")
            @RequestBody String reason) {
        adminMemberService.blockMember(memberId, reason);
    }

    @Operation(summary = "멤버 차단 해제", description = "차단된 멤버의 차단을 해제합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 차단 해제 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "멤버 없음", description = "해당 멤버를 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "차단되지 않음", description = "차단되지 않은 멤버입니다", value = AdminErrorCode.class, code = "NOT_BLOCKED_MEMBER")
    })
    @PatchMapping("/{memberId}/unblock")
    public void unblockMember(
            @Parameter(description = "멤버 ID", example = "1")
            @PathVariable Long memberId) {
        adminMemberService.unblockMember(memberId);
    }

    @Operation(summary = "멤버 휴면 처리", description = "멤버를 휴면 상태로 변경합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 휴면 처리 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "멤버 없음", description = "해당 멤버를 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "이미 휴면", description = "이미 휴면 상태인 멤버입니다", value = AdminErrorCode.class, code = "ALREADY_DORMANT_MEMBER"),
            @SwaggerErrorDescription(name = "이미 탈퇴", description = "이미 탈퇴한 멤버입니다", value = AdminErrorCode.class, code = "ALREADY_WITHDRAWN_MEMBER")
    })
    @PatchMapping("/{memberId}/dormant")
    public void setMemberDormant(
            @Parameter(description = "멤버 ID", example = "1")
            @PathVariable Long memberId) {
        adminMemberService.dormantMember(memberId);
    }

    @Operation(summary = "멤버 탈퇴 처리", description = "멤버를 탈퇴 상태로 변경합니다")
    @SwaggerSuccessResponse(status = 200, name = "멤버 탈퇴 처리 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "멤버 없음", description = "해당 멤버를 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "이미 탈퇴", description = "이미 탈퇴한 멤버입니다", value = AdminErrorCode.class, code = "ALREADY_WITHDRAWN_MEMBER")
    })
    @DeleteMapping("/{memberId}/withdraw")
    public void withdrawMember(
            @Parameter(description = "멤버 ID", example = "1")
            @PathVariable Long memberId) {
        adminMemberService.withdrawMember(memberId);
    }
}