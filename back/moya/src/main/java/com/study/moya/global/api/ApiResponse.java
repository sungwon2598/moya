package com.study.moya.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.moya.error.exception.BaseException;
import java.util.List;
import java.util.Map;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "API 응답")
public class ApiResponse<T> {
    @Schema(description = "HTTP 상태 코드", example = "200")
    private final int status;

    @Schema(description = "응답 데이터")
    private final T data;

    @Schema(description = "페이지네이션 정보")
    private final PageInfo pagination;

    @Schema(description = "에러 정보")
    private final ApiError error;


    private ApiResponse(int status, T data, ApiError error, PageInfo pagination) {
        this.status = status;
        this.data = data;
        this.pagination = pagination;
        this.error = error;
    }

    public static ApiResponse<Void> success() {
        return new ApiResponse<>(HttpStatus.OK.value(), null, null, null);
    }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(HttpStatus.OK.value(), data, null, null);
    }

    public static <T> ApiResponse<List<T>> of(Page<T> page) {
        return new ApiResponse<>(HttpStatus.OK.value(),
                page.getContent(),
                null,
                PageInfo.of(page));
    }

    public static <T> ApiResponse<T> error(BaseException e) {
        return new ApiResponse<>(e.getStatus().value(),
                null,
                ApiError.of(e.getCode(), e.getMessage()),
                null);
    }


    public static <T> ApiResponse<T> error(BaseException e, Map<String, Object> details) {
        return new ApiResponse<>(e.getStatus().value(),
                null,
                ApiError.of(e.getCode(), e.getMessage(), details),
                null);
    }
}

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
class Meta {
    private final int status;
    private final PageInfo pagination;

    Meta(int status, PageInfo pagination) {
        this.status = status;
        this.pagination = pagination;
    }
}