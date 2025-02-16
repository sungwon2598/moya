package com.study.moya.swagger.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerSuccessResponse {
    int status() default 200;
    String name() default "";
    Class<?> value() default Void.class;  // 응답 DTO 클래스 지정

}
