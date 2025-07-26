package com.study.moya.admin.dto.roadmap.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class WorksheetResponse {
    private Long dailyPlanId;
    private Integer weekNumber;
    private Integer dayNumber;
    private String keyword;
    private String worksheet;
}
