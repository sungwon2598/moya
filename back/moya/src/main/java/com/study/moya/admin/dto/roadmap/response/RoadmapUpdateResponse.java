package com.study.moya.admin.dto.roadmap.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "어드민 - 로드맵 수정 응답")
public class RoadmapUpdateResponse {

    @Schema(description = "성공 여부", example = "true")
    private boolean success;

    @Schema(description = "응답 메시지", example = "로드맵이 성공적으로 수정되었습니다")
    private String message;

    @Schema(description = "수정 시간")
    private LocalDateTime updatedAt;

    @Schema(description = "수정된 데이터 (선택적)")
    private Object updatedData;

    public static RoadmapUpdateResponse success(String message) {
        return RoadmapUpdateResponse.builder()
                .success(true)
                .message(message)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    public static RoadmapUpdateResponse success(String message, Object data) {
        return RoadmapUpdateResponse.builder()
                .success(true)
                .message(message)
                .updatedAt(LocalDateTime.now())
                .updatedData(data)
                .build();
    }
}
