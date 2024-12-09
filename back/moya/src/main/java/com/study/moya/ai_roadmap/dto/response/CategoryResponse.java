package com.study.moya.ai_roadmap.dto.response;

import com.study.moya.ai_roadmap.domain.Category;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CategoryResponse {
    private Long id;
    private String name;
    private int depth;
    private Long parentId;

    public static CategoryResponse from(Category category) {
        return CategoryResponse.builder()
                .id(category.getId())
                .name(category.getName())
                .depth(category.getDepth())
                .parentId(category.getParent() != null ? category.getParent().getId() : null)
                .build();
    }
}