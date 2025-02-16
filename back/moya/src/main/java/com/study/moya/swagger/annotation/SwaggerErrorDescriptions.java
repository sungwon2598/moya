package com.study.moya.swagger.annotation;

import java.lang.annotation.*;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface SwaggerErrorDescriptions {
    SwaggerErrorDescription[] value();
}