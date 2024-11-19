package com.study.moya.global.api;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.study.moya.error.exception.BaseException;
import java.util.List;
import java.util.Map;
import lombok.Getter;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {
    private final T data;
    private final Meta meta;
    private final ApiError error;

    private ApiResponse(T data, Meta meta, ApiError error) {
        this.data = data;
        this.meta = meta;
        this.error = error;
    }

    public static <T> ApiResponse<T> of(T data) {
        return new ApiResponse<>(data,
                new Meta(HttpStatus.OK.value(), null),
                null);
    }

    public static <T> ApiResponse<List<T>> of(Page<T> page) {
        return new ApiResponse<>(page.getContent(),
                new Meta(HttpStatus.OK.value(), PageInfo.of(page)),
                null);
    }

    public static <T> ApiResponse<T> error(BaseException e) {
        return new ApiResponse<>(null,
                new Meta(e.getStatus().value(), null),
                ApiError.of(e.getCode(), e.getMessage()));
    }

    public static <T> ApiResponse<T> error(BaseException e, Map<String, Object> details) {
        return new ApiResponse<>(null,
                new Meta(e.getStatus().value(), null),
                ApiError.of(e.getCode(), e.getMessage(), details));
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