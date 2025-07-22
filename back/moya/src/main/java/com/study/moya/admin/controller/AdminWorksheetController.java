package com.study.moya.admin.controller;

import com.study.moya.admin.dto.roadmap.request.WorksheetUpdateRequest;
import com.study.moya.admin.dto.roadmap.response.WorksheetResponse;
import com.study.moya.admin.exception.AdminErrorCode;
import com.study.moya.admin.service.AdminWorksheetService;
import com.study.moya.ai_roadmap.dto.request.WorkSheetRequest;
import com.study.moya.ai_roadmap.dto.response.WorksheetStatusResponse;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "Admin Worksheet", description = "관리자 워크시트 관리 API")
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("api/admin/worksheets")
//@PreAuthorize("hasRole('ADMIN')")
public class AdminWorksheetController {

    private final AdminWorksheetService adminWorksheetService;

    @Operation(summary = "워크시트 생성", description = "특정 로드맵의 모든 워크시트를 비동기로 생성합니다")
    @SwaggerSuccessResponse(status = 202, name = "워크시트 생성 요청 수락됨")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "워크시트 생성 실패", description = "워크시트 생성 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PostMapping("/{roadmapId}/generate")
    public ResponseEntity<Void> generateWorksheets(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId,
            @Parameter(description = "워크시트 생성 요청 정보")
            @Valid @RequestBody WorkSheetRequest request) {
        log.info("로드맵 ID: {}의 학습지 생성 시작", roadmapId);
        adminWorksheetService.generateAllWorksheets(roadmapId, request)
                .thenRun(() -> log.info("로드맵 ID: {}의 학습지 생성 완료", roadmapId))
                .exceptionally(ex -> {
                    log.error("학습지 생성 중 오류 발생: {}", ex.getMessage());
                    return null;
                });
        return ResponseEntity.accepted().build();
    }

    @Operation(summary = "워크시트 생성 상태 조회", description = "특정 로드맵의 워크시트 생성 진행 상태를 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "워크시트 상태 조회 성공", value = WorksheetStatusResponse.class)
    @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND")
    @GetMapping("/{roadmapId}/worksheets/status")
    public ResponseEntity<WorksheetStatusResponse> getWorksheetStatus(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId) {
        long progress = adminWorksheetService.getWorksheetProgress(roadmapId);
        boolean isCompleted = progress >= 100;

        WorksheetStatusResponse response = new WorksheetStatusResponse(
                isCompleted,
                progress,
                isCompleted ? "완료" : "진행 중"
        );

        return ResponseEntity.ok(response);
    }

    @Operation(summary = "로드맵별 전체 워크시트 조회", description = "특정 로드맵의 모든 워크시트 목록을 조회합니다")
    @SwaggerSuccessResponse(status = 200, name = "워크시트 목록 조회 성공", value = List.class)
    @SwaggerErrorDescription(name = "로드맵 없음", description = "해당 로드맵을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND")
    @GetMapping("/{roadmapId}")
    public ResponseEntity<List<WorksheetResponse>> getAllWorksheets(
            @Parameter(description = "로드맵 ID", example = "1")
            @PathVariable Long roadmapId) {
        log.info("로드맵 ID: {}의 모든 워크시트 조회 요청", roadmapId);

        List<WorksheetResponse> worksheets = adminWorksheetService.getAllWorksheetsByRoadmapId(roadmapId);

        log.info("로드맵 ID: {}의 워크시트 {}개 조회 완료", roadmapId, worksheets.size());
        return ResponseEntity.ok(worksheets);
    }

    @Operation(summary = "워크시트 수정", description = "특정 일간계획의 워크시트 내용을 수정합니다")
    @SwaggerSuccessResponse(status = 204, name = "워크시트 수정 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "일간계획 없음", description = "해당 일간계획을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "워크시트 수정 실패", description = "워크시트 수정 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @PutMapping("/update/{dailyPlanId}")
    public ResponseEntity<Void> updateWorksheet(
            @Parameter(description = "일간계획 ID", example = "1")
            @PathVariable Long dailyPlanId,
            @Parameter(description = "워크시트 수정 요청 정보")
            @Valid @RequestBody WorksheetUpdateRequest request) {
        log.info("일단 api 건들긴 했다!!!!");
        log.info("일간계획 ID: {}의 워크시트 수정 요청", dailyPlanId);

        adminWorksheetService.updateWorksheet(dailyPlanId, request);

        log.info("일간계획 ID: {}의 워크시트 수정 완료", dailyPlanId);
        return ResponseEntity.noContent().build();  // 204로 변경
    }

    @Operation(summary = "워크시트 삭제", description = "특정 일간계획의 워크시트 내용을 삭제합니다 (내용만 null로 변경)")
    @SwaggerSuccessResponse(status = 204, name = "워크시트 삭제 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "일간계획 없음", description = "해당 일간계획을 찾을 수 없습니다", value = AdminErrorCode.class, code = "POST_NOT_FOUND"),
            @SwaggerErrorDescription(name = "워크시트 삭제 실패", description = "워크시트 삭제 중 오류가 발생했습니다", value = AdminErrorCode.class, code = "STATISTICS_RETRIEVAL_FAILED")
    })
    @DeleteMapping("/delete/{dailyPlanId}")
    public ResponseEntity<Void> deleteWorksheet(
            @Parameter(description = "일간계획 ID", example = "1")
            @PathVariable Long dailyPlanId) {
        log.info("일간계획 ID: {}의 워크시트 삭제 요청", dailyPlanId);

        adminWorksheetService.deleteWorksheet(dailyPlanId);

        log.info("일간계획 ID: {}의 워크시트 삭제 완료", dailyPlanId);
        return ResponseEntity.noContent().build();
    }
}