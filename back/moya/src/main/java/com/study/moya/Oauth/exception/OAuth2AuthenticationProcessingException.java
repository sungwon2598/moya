package com.study.moya.Oauth.exception;

import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;

public class OAuth2AuthenticationProcessingException extends OAuth2AuthenticationException {

    public OAuth2AuthenticationProcessingException(String message) {
        super(new OAuth2Error("authentication_error"), message);
    }

    public OAuth2AuthenticationProcessingException(String message, Throwable cause) {
        super(new OAuth2Error("authentication_error"), message, cause);
    }
}
