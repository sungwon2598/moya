package com.study.moya.admin.controller;

import com.study.moya.admin.dto.roadmap.request.*;
import com.study.moya.admin.dto.roadmap.response.AdminMemberSubscriptionResponse;
import com.study.moya.admin.dto.roadmap.response.RoadmapUpdateResponse;
import com.study.moya.admin.service.AdminRoadmapService;
import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.response.*;
import com.study.moya.ai_roadmap.repository.RoadMapRepository;
import com.study.moya.error.constants.AuthErrorCode;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/roadmap")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminRoadmapController {

    private final AdminRoadmapService adminRoadmapService;

    @PostMapping("/generatge2")
    public void generateRoadmap(@RequestBody @Valid RoadmapRequest roadmapRequest) {}

    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<WeeklyRoadmapResponse>> generateWeeklyRoadmap(
            @Valid @RequestBody RoadmapRequest request, @AuthenticationPrincipal Long memberId
    ) {
        log.info("작동해버리기~============================");
        log.info("현재 로그인한 회원 ID: {}", memberId);
        return adminRoadmapService.generateWeeklyRoadmapAsync(request)
                .thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(ex -> {
                    // 예외 발생 시 처리
                    return ResponseEntity.status(500).body(null);
                });
    }

    @GetMapping("/categories/{categoryId}/roadmaps")
    public ResponseEntity<List<AdminMemberSubscriptionResponse>> getRoadMapsByCategory(
            @PathVariable Long categoryId) {
        log.info("어드민 - 카테고리별 로드맵 조회 요청 - 카테고리 ID: {}", categoryId);

        List<AdminMemberSubscriptionResponse> roadmaps = adminRoadmapService.getRoadMapsByCategory(categoryId);

        return ResponseEntity.ok(roadmaps);
    }

    @GetMapping("/{roadMapId}")
    public ResponseEntity<WeeklyRoadmapResponse> getRoadMap(@PathVariable Long roadMapId){
        return ResponseEntity.ok(adminRoadmapService.getRoadmapById(roadMapId));
    }

    // ============ 로드맵 수정 기능들 ============

    /**
     * 일별 키워드 수정
     */
    @PutMapping("/{roadmapId}/week/{weekNumber}/day/{dayNumber}/keyword")
    public ResponseEntity<RoadmapUpdateResponse> updateDailyKeyword(
            @PathVariable Long roadmapId,
            @PathVariable Integer weekNumber,
            @PathVariable Integer dayNumber,
            @Valid @RequestBody UpdateDailyKeywordRequest request) {

        log.info("어드민 - 일별 키워드 수정 요청 - 로드맵: {}, {}주차 {}일차", roadmapId, weekNumber, dayNumber);

        String updatedKeyword = adminRoadmapService.updateDailyKeyword(roadmapId, weekNumber, dayNumber, request.getKeyword());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 %d주차 %d일차 키워드가 수정되었습니다", roadmapId, weekNumber, dayNumber),
                updatedKeyword  // 수정된 데이터 포함
        ));
    }

    /**
     * 주차별 키워드 수정
     */
    @PutMapping("/{roadmapId}/week/{weekNumber}/keyword")
    public ResponseEntity<RoadmapUpdateResponse> updateWeeklyKeyword(
            @PathVariable Long roadmapId,
            @PathVariable Integer weekNumber,
            @Valid @RequestBody UpdateWeeklyKeywordRequest request) {

        log.info("어드민 - 주차별 키워드 수정 요청 - 로드맵: {}, {}주차", roadmapId, weekNumber);

        String updatedKeyword = adminRoadmapService.updateWeeklyKeyword(roadmapId, weekNumber, request.getKeyword());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 %d주차 키워드가 수정되었습니다", roadmapId, weekNumber),
                Map.of("updatedKeyword", updatedKeyword)
        ));
    }

    /**
     * 전체 팁 수정
     */
    @PutMapping("/{roadmapId}/tips")
    public ResponseEntity<RoadmapUpdateResponse> updateOverallTips(
            @PathVariable Long roadmapId,
            @Valid @RequestBody UpdateOverallTipsRequest request) {

        log.info("어드민 - 전체 팁 수정 요청 - 로드맵: {}", roadmapId);

        List<String> updatedTips = adminRoadmapService.updateOverallTips(roadmapId, request.getTips());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 전체 팁이 수정되었습니다", roadmapId),
                Map.of("updatedTips", updatedTips)
        ));
    }

    /**
     * 로드맵 기본 정보 수정
     */
    @PutMapping("/{roadmapId}/info")
    public ResponseEntity<RoadmapUpdateResponse> updateRoadmapInfo(
            @PathVariable Long roadmapId,
            @Valid @RequestBody UpdateRoadmapInfoRequest request) {

        log.info("어드민 - 로드맵 기본 정보 수정 요청 - 로드맵: {}", roadmapId);

        Map<String, Object> updatedInfo = adminRoadmapService.updateRoadmapInfo(roadmapId, request);

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 기본 정보가 수정되었습니다", roadmapId),
                updatedInfo
        ));
    }

    /**
     * 로드맵 삭제
     */
    @DeleteMapping("/{roadmapId}")
    public ResponseEntity<RoadmapUpdateResponse> deleteRoadmap(@PathVariable Long roadmapId) {
        log.info("어드민 - 로드맵 삭제 요청 - 로드맵: {}", roadmapId);

        adminRoadmapService.deleteRoadmap(roadmapId);

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d가 삭제되었습니다", roadmapId)
        ));
    }

    /**
     * 전체 로드맵 목록 조회 (관리자용)
     */
    @GetMapping("/all")
    public ResponseEntity<List<RoadMapSummaryDTO>> getAllRoadmaps() {
        log.info("어드민 - 전체 로드맵 목록 조회");

        List<RoadMapSummaryDTO> roadmaps = adminRoadmapService.getAllRoadmaps();

        return ResponseEntity.ok(roadmaps);
    }

    @GetMapping("/{memberId}/subscriptions")
    public ResponseEntity<List<AdminMemberSubscriptionResponse>> getMemberSubscriptions(
            @PathVariable Long memberId) {
        log.info("어드민 - 멤버 구독 로드맵 조회 요청 - 멤버 ID: {}", memberId);

        List<AdminMemberSubscriptionResponse> subscriptions = adminRoadmapService.getMemberSubscriptions(memberId);

        return ResponseEntity.ok(subscriptions);
    }
}