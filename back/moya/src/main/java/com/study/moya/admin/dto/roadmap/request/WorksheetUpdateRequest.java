package com.study.moya.admin.dto.roadmap.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "어드민 - 워크시트 수정 요청")
public class WorksheetUpdateRequest {

    @NotBlank(message = "워크시트 내용은 필수입니다.")
    private String worksheet;
}
