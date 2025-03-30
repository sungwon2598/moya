package com.study.moya.error;

import com.study.moya.error.constants.AuthErrorCode;
import com.study.moya.error.constants.CommonErrorCode;
import com.study.moya.error.exception.AuthException;
import com.study.moya.error.exception.BaseException;
import com.study.moya.global.api.ApiResponse;
import jakarta.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.naming.AuthenticationException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.http.converter.HttpMessageNotReadableException;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final String ERROR_LOG_FORMAT = "[{}] {} :: {}";

    /**
     * BaseException 하위 커스텀 예외 처리
     */
    @ExceptionHandler(BaseException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBaseException(BaseException e, HttpServletRequest request) {
        log.error(ERROR_LOG_FORMAT, e.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(e.getStatus())
                .body(ApiResponse.error(e));
    }

    /**
     * Spring Security 인증 예외 처리
     */
    @ExceptionHandler(AuthenticationException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAuthenticationException(
            AuthenticationException e, HttpServletRequest request) {
        AuthException authException = AuthException.of(AuthErrorCode.UNAUTHORIZED);

        log.error(ERROR_LOG_FORMAT, authException.getStatus(), request.getRequestURI(), e.getMessage());

        return ResponseEntity.status(authException.getStatus())
                .body(ApiResponse.error(authException));
    }

    /**
     * Spring Security 인가 예외 처리
     */
    @ExceptionHandler(AccessDeniedException.class)
    protected ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(
            AccessDeniedException e, HttpServletRequest request) {
        AuthException authException = AuthException.of(AuthErrorCode.ACCESS_DENIED);
        log.error(ERROR_LOG_FORMAT, authException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(authException.getStatus())
                .body(ApiResponse.error(authException));
    }

    /**
     * Valid 또는 Validated 어노테이션 예외 처리
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentNotValidException(
            MethodArgumentNotValidException e, HttpServletRequest request) {
        Map<String, Object> details = createValidationDetails(e.getBindingResult().getFieldErrors());
        BaseException baseException = BaseException.of(CommonErrorCode.INVALID_INPUT_VALUE);

        log.error(ERROR_LOG_FORMAT, baseException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException, details));
    }

    /**
     * ModelAttribute 바인딩 예외 처리
     */
    @ExceptionHandler(BindException.class)
    protected ResponseEntity<ApiResponse<Void>> handleBindException(
            BindException e, HttpServletRequest request) {
        Map<String, Object> details = createValidationDetails(e.getBindingResult().getFieldErrors());
        BaseException baseException = BaseException.of(CommonErrorCode.INVALID_INPUT_VALUE);

        log.error(ERROR_LOG_FORMAT, baseException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException, details));
    }

    /**
     * PathVariable 및 RequestParam 타입 불일치 예외 처리
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e, HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("errors", List.of(Map.of(
                "field", e.getName(),
                "value", e.getValue() != null ? e.getValue().toString() : "",
                "reason", "타입이 일치하지 않습니다"
        )));

        BaseException baseException = BaseException.of(CommonErrorCode.INVALID_TYPE_VALUE);

        log.error(ERROR_LOG_FORMAT, baseException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException, details));
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<ApiResponse<Void>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e, HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();
        details.put("errors", List.of(Map.of(
                "field", e.getParameterName(),
                "value", "",
                "reason", "필수 파라미터가 누락되었습니다"
        )));

        BaseException baseException = BaseException.of(CommonErrorCode.INVALID_INPUT_VALUE);

        log.error(ERROR_LOG_FORMAT, baseException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException, details));
    }

    /**
     * JSON 파싱 관련 예외 처리 (enum 값 불일치 포함)
     */
    @ExceptionHandler(HttpMessageNotReadableException.class)
    protected ResponseEntity<ApiResponse<Void>> handleHttpMessageNotReadableException(
            HttpMessageNotReadableException e, HttpServletRequest request) {
        Map<String, Object> details = new HashMap<>();

        // Enum 값 불일치 여부를 확인하는 조건
        boolean isEnumValueError = e.getMessage() != null &&
                (e.getMessage().contains("not one of the values accepted for Enum class") ||
                        e.getMessage().contains("Cannot deserialize value of type"));

        String reason = isEnumValueError ?
                "선택하신 학습 목표가 유효하지 않습니다" :
                "요청 본문을 파싱할 수 없습니다";

        details.put("errors", List.of(Map.of(
                "field", isEnumValueError ? "learningObjective" : "requestBody",
                "value", "",
                "reason", reason
        )));

        BaseException baseException = BaseException.of(CommonErrorCode.INVALID_INPUT_VALUE);

        log.error(ERROR_LOG_FORMAT, baseException.getStatus(), request.getRequestURI(), e.getMessage());
        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException, details));
    }

    /**
     * 예상하지 못한 예외 처리
     */
    @ExceptionHandler(Exception.class)
    protected ResponseEntity<ApiResponse<Void>> handleException(Exception e, HttpServletRequest request) {
        log.error(ERROR_LOG_FORMAT, "INTERNAL_SERVER_ERROR", request.getRequestURI(), e.getMessage());
        log.error("Unexpected exception occurred", e);  // 스택 트레이스 로깅

        BaseException baseException = BaseException.of(CommonErrorCode.INTERNAL_SERVER_ERROR);

        return ResponseEntity.status(baseException.getStatus())
                .body(ApiResponse.error(baseException));
    }

    private Map<String, Object> createValidationDetails(List<FieldError> fieldErrors) {
        Map<String, Object> details = new HashMap<>();
        details.put("errors", fieldErrors.stream()
                .map(error -> Map.of(
                        "field", error.getField(),
                        "value", error.getRejectedValue() != null ? error.getRejectedValue().toString() : "",
                        "reason", error.getDefaultMessage()
                ))
                .collect(Collectors.toList()));
        return details;
    }
}