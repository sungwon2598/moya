package com.study.moya.admin.controller;

import com.study.moya.admin.dto.roadmap.request.*;
import com.study.moya.admin.dto.roadmap.response.AdminMemberSubscriptionResponse;
import com.study.moya.admin.dto.roadmap.response.RoadmapUpdateResponse;
import com.study.moya.admin.exception.AdminErrorCode;
import com.study.moya.admin.service.AdminRoadmapService;
import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.response.*;
import com.study.moya.ai_roadmap.repository.RoadMapRepository;
import com.study.moya.error.constants.AuthErrorCode;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Admin Roadmap", description = "관리자 로드맵 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/roadmap")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminRoadmapController {

    private final AdminRoadmapService adminRoadmapService;

    @Operation(summary = "로드맵 생성 테스트", description = "로드맵 생성 요청 테스트용 엔드포인트")
    @SwaggerSuccessResponse(status = 200, name = "테스트 완료")
    @PostMapping("/generatge2")
    public void generateRoadmap(
            @Parameter(description = "로드맵 생성 요청 정보")
            @RequestBody @Valid RoadmapRequest roadmapRequest) {}

    @Operation(summary = "로드맵 비동기 생성", description = "AI를 활용하여 주간 로드맵을 비동기로 생성합니다")
    @SwaggerSuccessResponse(status = 200, name = "로드맵 생성 성공", value = WeeklyRoadmapResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "카테고리 없음", description = "해당 카테고리를 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "생성 실패", description = "로드맵 생성 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<WeeklyRoadmapResponse>> generateWeeklyRoadmap(
            @Parameter(description = "로드맵 생성 요청 정보")
            @Valid @RequestBody RoadmapRequest request,
            @Parameter(description = "로그인한 회원 ID")
            @AuthenticationPrincipal Long memberId) {
        log.info("작동해버리기~============================");
        log.info("현재 로그인한 회원 ID: {}", memberId);
        return adminRoadmapService.generateWeeklyRoadmapAsync(request)
                .thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(ex -> {
                    // 예외 발생 시 처리
                    return ResponseEntity.status(500).body(null);
                });
    }

    @Operation(summary = "카테고리별 로드맵 조회", description = "특정 카테고리에 속한 모든 로드맵을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "카테고리별 로드맵 조회 성공", value = List.class)
    @SwaggerErrorDescription(name = "카테고리 없음", description = "해당 카테고리를 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND")
    @GetMapping("/categories/{categoryId}/roadmaps")
    public ResponseEntity<List<AdminMemberSubscriptionResponse>> getRoadMapsByCategory(
            @Parameter(description = "카테고리 ID", example = "1")
            @PathVariable Long categoryId) {
        log.info("어드민 - 카테고리별 로드맵 조회 요청 - 카테고리 ID: {}", categoryId);

        List<AdminMemberSubscriptionResponse> roadmaps = adminRoadmapService.getRoadMapsByCategory(categoryId);

        return ResponseEntity.ok(roadmaps);
    }

    @Operation(summary = "로드맵 상세 조회", description = "특정 로드맵의 상세 정보를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "로드맵 상세 조회 성공", value = WeeklyRoadmapResponse.class)
    @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND")
    @GetMapping("/{roadMapId}")
    public ResponseEntity<WeeklyRoadmapResponse> getRoadMap(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadMapId){
        return ResponseEntity.ok(adminRoadmapService.getRoadmapById(roadMapId));
    }

    // ============ 로드맵 수정 기능들 ============

    @Operation(summary = "일별 키워드 수정", description = "특정 로드맵의 일별 키워드를 수정합니다")
    @SwaggerSuccessResponse(status = 200, name = "일별 키워드 수정 성공", value = RoadmapUpdateResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "수정 실패", description = "키워드 수정 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PutMapping("/{roadmapId}/week/{weekNumber}/day/{dayNumber}/keyword")
    public ResponseEntity<RoadmapUpdateResponse> updateDailyKeyword(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @Parameter(description = "주차 번호", example = "1")
            @PathVariable Integer weekNumber,
            @Parameter(description = "일차 번호", example = "1")
            @PathVariable Integer dayNumber,
            @Parameter(description = "일별 키워드 수정 요청")
            @Valid @RequestBody UpdateDailyKeywordRequest request) {

        log.info("어드민 - 일별 키워드 수정 요청 - 로드맵: {}, {}주차 {}일차", roadmapId, weekNumber, dayNumber);

        String updatedKeyword = adminRoadmapService.updateDailyKeyword(roadmapId, weekNumber, dayNumber, request.getKeyword());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 %d주차 %d일차 키워드가 수정되었습니다", roadmapId, weekNumber, dayNumber),
                updatedKeyword  // 수정된 데이터 포함
        ));
    }

    @Operation(summary = "주차별 키워드 수정", description = "특정 로드맵의 주차별 키워드를 수정합니다")
    @SwaggerSuccessResponse(status = 200, name = "주차별 키워드 수정 성공", value = RoadmapUpdateResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "수정 실패", description = "키워드 수정 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PutMapping("/{roadmapId}/week/{weekNumber}/keyword")
    public ResponseEntity<RoadmapUpdateResponse> updateWeeklyKeyword(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @Parameter(description = "주차 번호", example = "1")
            @PathVariable Integer weekNumber,
            @Parameter(description = "주차별 키워드 수정 요청")
            @Valid @RequestBody UpdateWeeklyKeywordRequest request) {

        log.info("어드민 - 주차별 키워드 수정 요청 - 로드맵: {}, {}주차", roadmapId, weekNumber);

        String updatedKeyword = adminRoadmapService.updateWeeklyKeyword(roadmapId, weekNumber, request.getKeyword());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 %d주차 키워드가 수정되었습니다", roadmapId, weekNumber),
                Map.of("updatedKeyword", updatedKeyword)
        ));
    }

    @Operation(summary = "전체 팁 수정", description = "특정 로드맵의 전체 팁 목록을 수정합니다")
    @SwaggerSuccessResponse(status = 200, name = "전체 팁 수정 성공", value = RoadmapUpdateResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "수정 실패", description = "팁 수정 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PutMapping("/{roadmapId}/tips")
    public ResponseEntity<RoadmapUpdateResponse> updateOverallTips(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @Parameter(description = "전체 팁 수정 요청")
            @Valid @RequestBody UpdateOverallTipsRequest request) {

        log.info("어드민 - 전체 팁 수정 요청 - 로드맵: {}", roadmapId);

        List<String> updatedTips = adminRoadmapService.updateOverallTips(roadmapId, request.getTips());

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 전체 팁이 수정되었습니다", roadmapId),
                Map.of("updatedTips", updatedTips)
        ));
    }

    @Operation(summary = "로드맵 기본 정보 수정", description = "로드맵의 주제, 기간, 학습목표, 평가 등 기본 정보를 수정합니다")
    @SwaggerSuccessResponse(status = 200, name = "로드맵 기본 정보 수정 성공", value = RoadmapUpdateResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "수정 실패", description = "기본 정보 수정 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PutMapping("/{roadmapId}/info")
    public ResponseEntity<RoadmapUpdateResponse> updateRoadmapInfo(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @Parameter(description = "로드맵 기본 정보 수정 요청")
            @Valid @RequestBody UpdateRoadmapInfoRequest request) {

        log.info("어드민 - 로드맵 기본 정보 수정 요청 - 로드맵: {}", roadmapId);

        Map<String, Object> updatedInfo = adminRoadmapService.updateRoadmapInfo(roadmapId, request);

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d의 기본 정보가 수정되었습니다", roadmapId),
                updatedInfo
        ));
    }

    @Operation(summary = "로드맵 삭제", description = "특정 로드맵과 관련된 모든 데이터를 삭제합니다")
    @SwaggerSuccessResponse(status = 200, name = "로드맵 삭제 성공", value = RoadmapUpdateResponse.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "삭제 실패", description = "로드맵 삭제 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @DeleteMapping("/{roadmapId}")
    public ResponseEntity<RoadmapUpdateResponse> deleteRoadmap(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId) {
        log.info("어드민 - 로드맵 삭제 요청 - 로드맵: {}", roadmapId);

        adminRoadmapService.deleteRoadmap(roadmapId);

        return ResponseEntity.ok(RoadmapUpdateResponse.success(
                String.format("로드맵 %d가 삭제되었습니다", roadmapId)
        ));
    }

    @Operation(summary = "전체 로드맵 목록 조회", description = "관리자용 전체 로드맵 목록을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "전체 로드맵 조회 성공", value = List.class)
    @SwaggerErrorDescription(name = "조회 실패", description = "로드맵 목록 조회 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    @GetMapping("/all")
    public ResponseEntity<List<RoadMapSummaryDTO>> getAllRoadmaps() {
        log.info("어드민 - 전체 로드맵 목록 조회");

        List<RoadMapSummaryDTO> roadmaps = adminRoadmapService.getAllRoadmaps();

        return ResponseEntity.ok(roadmaps);
    }

    @Operation(summary = "회원 구독 로드맵 조회", description = "특정 회원이 구독한 모든 로드맵을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "회원 구독 로드맵 조회 성공", value = List.class)
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "회원 없음", description = "해당 회원을 찾을 수 없습니다", value = AdminErrorCode.class, code = "MEMBER_NOT_FOUND"),
            @SwaggerErrorDescription(name = "조회 실패", description = "구독 정보 조회 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @GetMapping("/{memberId}/subscriptions")
    public ResponseEntity<List<AdminMemberSubscriptionResponse>> getMemberSubscriptions(
            @Parameter(description = "회원 ID", example = "1")
            @PathVariable Long memberId) {
        log.info("어드민 - 멤버 구독 로드맵 조회 요청 - 멤버 ID: {}", memberId);

        List<AdminMemberSubscriptionResponse> subscriptions = adminRoadmapService.getMemberSubscriptions(memberId);

        return ResponseEntity.ok(subscriptions);
    }
}