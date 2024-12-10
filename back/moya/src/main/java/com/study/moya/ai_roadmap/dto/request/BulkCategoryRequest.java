package com.study.moya.ai_roadmap.dto.request;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class BulkCategoryRequest {
    private List<CategoryOperation> operations;

    @Getter
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryOperation {
        private OperationType operation;
        private Long id;  // UPDATE, DELETE 시 사용
        private String name;  // CREATE, UPDATE 시 사용
        private Long parentId;  // CREATE 시 사용
    }

    public enum OperationType {
        CREATE, UPDATE, DELETE
    }
}