package com.study.moya.Oauth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class SignUpCompleteRequest {

    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    private String email;

    @NotBlank(message = "닉네임은 필수 입력값입니다.")
    @Size(min = 2, max = 20, message = "닉네임은 2자 이상 20자 이하로 입력해주세요.")
    private String nickname;

    @NotNull(message = "서비스 이용약관 동의는 필수입니다.")
    private Boolean termsAgreed;

    @NotNull(message = "개인정보 처리방침 동의는 필수입니다.")
    private Boolean privacyPolicyAgreed;

    @NotNull(message = "마케팅 수신 동의 여부를 선택해주세요.")
    private Boolean marketingAgreed;

    @Builder
    public SignUpCompleteRequest(String email, String nickname,
                                 Boolean termsAgreed, Boolean privacyPolicyAgreed,
                                 Boolean marketingAgreed) {
        this.email = email;
        this.nickname = nickname;
        this.termsAgreed = termsAgreed;
        this.privacyPolicyAgreed = privacyPolicyAgreed;
        this.marketingAgreed = marketingAgreed;
    }
}