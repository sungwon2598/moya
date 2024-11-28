package com.study.moya.Oauth.validator;

import com.study.moya.Oauth.dto.OAuth2SignupCompleteRequest;
import com.study.moya.Oauth.dto.OAuth2SignupCompleteResponse;
import com.study.moya.Oauth.dto.OAuthTempMemberInfo;
import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class SignupValidator {
    private final MemberRepository memberRepository;

    public void validate(OAuth2SignupCompleteRequest request, OAuthTempMemberInfo tempInfo) {
        validateRequiredTerms(request);
        validateNickname(request.getNickname());
    }

    private void validateRequiredTerms(OAuth2SignupCompleteRequest request) {
        if (!request.isTermsAgreed() || !request.isPrivacyPolicyAgreed()) {
            throw new IllegalArgumentException("필수 약관에 동의해주세요.");
        }
    }

    private void validateNickname(String nickname) {
        if (memberRepository.existsByNickname(nickname)) {
            throw new IllegalArgumentException("이미 사용 중인 닉네임입니다.");
        }
    }
}
