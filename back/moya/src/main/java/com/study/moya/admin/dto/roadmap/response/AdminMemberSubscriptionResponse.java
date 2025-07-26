package com.study.moya.admin.dto.roadmap.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class AdminMemberSubscriptionResponse {
    private Long roadmapId;
    private String mainCategory;
    private String subCategory;
    private Integer duration;
}
