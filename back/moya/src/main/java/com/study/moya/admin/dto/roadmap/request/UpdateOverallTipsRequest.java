package com.study.moya.admin.dto.roadmap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어드민 - 전체 팁 수정 요청")
public class UpdateOverallTipsRequest {

    @NotEmpty(message = "팁 목록은 비어있을 수 없습니다")
    @Schema(description = "새로운 팁 목록", example = "[\"매일 꾸준히 학습하세요\", \"실습을 중심으로 진행하세요\"]")
    private List<String> tips;
}
