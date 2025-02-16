package com.study.moya.swagger.annotation;

import com.study.moya.error.constants.ErrorCode;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerErrorDescription {
    String name();
    String description() default "";
    Class<? extends Enum<? extends ErrorCode>> value();
    String code();
}


