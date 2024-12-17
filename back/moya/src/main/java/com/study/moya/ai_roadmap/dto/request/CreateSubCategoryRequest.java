package com.study.moya.ai_roadmap.dto.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CreateSubCategoryRequest {
    private Long parentId;
    private String name;
}