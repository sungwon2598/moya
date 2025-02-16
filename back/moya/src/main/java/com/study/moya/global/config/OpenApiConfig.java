package com.study.moya.global.config;

import com.study.moya.swagger.annotation.SwaggerErrorDescription;
import com.study.moya.swagger.annotation.SwaggerErrorDescriptions;
import com.study.moya.swagger.annotation.SwaggerSuccessResponse;
import com.study.moya.error.constants.ErrorCode;
import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.servers.Server;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.Content;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.oas.models.responses.ApiResponse;
import io.swagger.v3.oas.models.security.SecurityScheme;
import org.springdoc.core.customizers.OperationCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.HandlerMethod;

import java.util.Map;

@OpenAPIDefinition(
        servers = {
                @Server(url = "https://api.moyastudy.com", description = "운영 서버"),
                @Server(url = "http://localhost:8080", description = "로컬 서버")
        }
)
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI usersMicroserviceOpenAPI() {
        return new OpenAPI()
                .components(new Components()
                        // JWT 인증 스키마 설정
                        .addSecuritySchemes("bearer-key",
                                new SecurityScheme()
                                        .type(SecurityScheme.Type.HTTP)
                                        .scheme("bearer")
                                        .bearerFormat("JWT")))
                .info(new Info()
                        .title("Team S API")
                        .description("Team S의 API 명세서입니다.")
                        .version("1.0.0"));
    }

    @Bean
    public OperationCustomizer customGlobalHeaders() {
        return (Operation operation, HandlerMethod handlerMethod) -> {
            operation.getResponses().clear();

            SwaggerSuccessResponse successResponse =
                    handlerMethod.getMethodAnnotation(SwaggerSuccessResponse.class);

            if (successResponse != null) {
                String statusCode = String.valueOf(successResponse.status());
                ApiResponse response = new ApiResponse()
                        .description(successResponse.name().isEmpty() ?
                                "Success" : successResponse.name());

                if (successResponse.value() != Void.class) {
                    // 응답 스키마 생성 (항상 ApiResponse 래퍼 사용)
                    Schema<?> dtoSchema = new Schema<>()
                            .$ref("#/components/schemas/" +
                                    successResponse.value().getSimpleName());

                    Schema<?> responseSchema = new Schema<>()
                            .type("object")
                            .addProperty("status", new Schema<>()
                                    .type("integer")
                                    .example(successResponse.status()))
                            .addProperty("data", dtoSchema);

                    response.content(new Content()
                            .addMediaType("application/json",
                                    new MediaType()
                                            .schema(responseSchema)));
                } else {
                    // 문자열 응답 처리
                    response.content(new Content()
                            .addMediaType("application/json",
                                    new MediaType().schema(new Schema<>()
                                            .type("object")
                                            .addProperty("status", new Schema<>()
                                                    .type("integer")
                                                    .example(successResponse.status()))
                                            .addProperty("data", new Schema<>()
                                                    .type("string")
                                                    .example(successResponse.name())))));
                }

                operation.getResponses().addApiResponse(statusCode, response);
            }

            SwaggerErrorDescriptions errorExplanations =
                    handlerMethod.getMethodAnnotation(SwaggerErrorDescriptions.class);
            if (errorExplanations != null) {
                for (SwaggerErrorDescription explanation : errorExplanations.value()) {
                    addErrorResponse(operation, explanation);
                }
            }

            return operation;
        };
    }

    private void addErrorResponse(Operation operation, SwaggerErrorDescription explanation) {
        try {
            ErrorCode errorCode = (ErrorCode) findEnumConstant(explanation.value(), explanation.code());
            String statusCode = String.valueOf(errorCode.getStatus().value());

            // 에러 코드를 포함한 고유한 응답 키 생성
            String responseKey = statusCode + "_" + errorCode.getFullCode();

            ApiResponse response = new ApiResponse()
                    .description(explanation.name() +
                            (explanation.description().isEmpty() ? "" : " - " + explanation.description()))
                    .content(new Content()
                            .addMediaType("application/json",
                                    new MediaType().schema(new Schema<>()
                                            .example(createErrorExample(errorCode)))));

            operation.getResponses().addApiResponse(responseKey, response);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Error processing ErrorCode enum: " + explanation.value() + "." + explanation.code(),
                    e
            );
        }
    }

    @SuppressWarnings("unchecked")
    private Enum<? extends ErrorCode> findEnumConstant(
            Class<? extends Enum<? extends ErrorCode>> enumClass,
            String constantName
    ) {
        return Enum.valueOf((Class<? extends Enum>) enumClass, constantName);
    }

    private Object createErrorExample(ErrorCode errorCode) {
        return Map.of(
                "status", errorCode.getStatus().value(),
                "error", Map.of(
                        "code", errorCode.getFullCode(),
                        "message", errorCode.getMessage()
                )
        );
    }
}