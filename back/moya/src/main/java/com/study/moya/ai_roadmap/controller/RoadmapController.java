package com.study.moya.ai_roadmap.controller;

import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.request.WorkSheetRequest;
import com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryDTO;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.dto.response.WorksheetStatusResponse;
import com.study.moya.ai_roadmap.service.RoadmapService;
import com.study.moya.ai_roadmap.service.WorksheetService;
import com.study.moya.error.constants.AuthErrorCode;
import com.study.moya.error.constants.CommonErrorCode;
import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequestMapping("/api/roadmap")
@RequiredArgsConstructor
public class RoadmapController {

    private final RoadmapService roadMapService;
    private final WorksheetService worksheetService;

    @PostMapping("/generatge2")
    public void generateRoadmap(@RequestBody @Valid RoadmapRequest roadmapRequest) {}

    @Operation(
            summary = "주간 로드맵 생성",
            description = "요청에 따라 주간 로드맵을 생성합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "성공적으로 로드맵 생성",
                            content = @Content(
                                    mediaType = "application/json",
                                    examples = {
                                            @ExampleObject(
                                                    name = "roadmap-response-example",
                                                    summary = "로드맵 응답 예시",
                                                    externalValue = "/static/swagger/examples/roadmap-example.json"
                                            )
                                    }
                            )
                    )
            }
    )
    @PostMapping("/generate")
    public CompletableFuture<ResponseEntity<WeeklyRoadmapResponse>> generateWeeklyRoadmap(
            @Valid @RequestBody RoadmapRequest request, @AuthenticationPrincipal Long memberId
    ) {
        log.info("작동해버리기~============================");
        log.info("현재 로그인한 회원 ID: {}", memberId);
        return roadMapService.generateWeeklyRoadmapAsync(request, memberId)
                .thenApply(response -> ResponseEntity.ok(response))
                .exceptionally(ex -> {
                    // 예외 발생 시 처리
                    return ResponseEntity.status(500).body(null);
                });
    }

    @PostMapping("/{roadmapId}/worksheets")
    public ResponseEntity<Void> generateWorksheets(@PathVariable Long roadmapId, @Valid @RequestBody WorkSheetRequest request, 
                                                  @AuthenticationPrincipal Long memberId) {
        log.info("로드맵 ID: {}의 학습지 생성 시작, 회원 ID: {}", roadmapId, memberId);
        worksheetService.generateAllWorksheets(roadmapId, request, memberId)
                .thenRun(() -> log.info("로드맵 ID: {}의 학습지 생성 완료", roadmapId))
                .exceptionally(ex -> {
                    log.error("학습지 생성 중 오류 발생: {}", ex.getMessage());
                    return null;
                });
        return ResponseEntity.accepted().build();
    }

    @GetMapping("/categories/{categoryId}/roadmaps")
    public List<RoadMapSimpleDto> getRoadMaps(@PathVariable Long categoryId) {
        return roadMapService.getRoadMapsByCategory(categoryId);
    }

    @Operation(summary = "내 로드맵 목록 조회", description = "현재 로그인한 회원의 로드맵 목록을 조회합니다.")
    @SwaggerSuccessResponse(value = List.class, name = "내 로드맵 목록 조회 성공")
    @SwaggerErrorDescriptions({
            @SwaggerErrorDescription(name = "인증 필요", description = "로그인이 필요한 서비스입니다.", value = AuthErrorCode.class, code = "UNAUTHORIZED")
    })
    @GetMapping("/myroadmaps")
    public ResponseEntity<?> getMyRoadmaps(@AuthenticationPrincipal Long memberId) {
//        memberId = 87L; //급하게 테스트 할 때

        // memberId가 null인 경우 비인증 사용자로 간주
        if (memberId == null) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "로그인이 필요한 서비스입니다.");
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(errorResponse);
        }

        // 인증된 사용자인 경우 정상 처리
        List<RoadMapSummaryDTO> roadMapSummaryDTOS = roadMapService.getMemberRoadMapSummaries(memberId);
        return ResponseEntity.ok(roadMapSummaryDTOS);
    }

    @GetMapping("/myroadmaps/{roadMapId}")
    public ResponseEntity<WeeklyRoadmapResponse> getRoadMap(@PathVariable Long roadMapId){
        return ResponseEntity.ok(roadMapService.getRoadmapById(roadMapId));
    }

    @GetMapping("/{roadmapId}/worksheets/status")
    public ResponseEntity<WorksheetStatusResponse> getWorksheetStatus(@PathVariable Long roadmapId) {
        long progress = worksheetService.getWorksheetProgress(roadmapId);
        boolean isCompleted = progress >= 100;

        WorksheetStatusResponse response = new WorksheetStatusResponse(
                isCompleted,
                progress,
                isCompleted ? "완료" : "진행 중"
        );

        return ResponseEntity.ok(response);
    }
}