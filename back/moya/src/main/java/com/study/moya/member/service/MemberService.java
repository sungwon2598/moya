package com.study.moya.member.service;

import com.study.moya.auth.dto.SignupRequest;
import com.study.moya.auth.exception.EmailAlreadyExistsException;
import com.study.moya.auth.exception.UserNotFoundException;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.Instant;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class MemberService implements UserDetailsService {

    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public void registerNewUser(SignupRequest signUpRequest) {
        log.info("새 사용자 등록 시작 - 이메일: {}, 닉네임: {}", signUpRequest.email(), signUpRequest.nickname());
        try {
            validateEmailNotExists(signUpRequest.email());
            log.info("이메일 중복 검사 완료 - 이메일: {}", signUpRequest.email());

            Member member = createMemberFromRequest(signUpRequest);
            log.info("회원 엔티티 생성 완료 - 이메일: {}", member.getEmail());

            Member savedMember = memberRepository.save(member);
            log.info("회원 저장 완료 - 이메일: {}, ID: {}", savedMember.getEmail(), savedMember.getId());
        } catch (Exception e) {
            log.error("회원 가입 처리 중 오류 발생 - 이메일: {}, 오류: {}", signUpRequest.email(), e.getMessage(), e);
            throw e;
        }
    }

    private void validateEmailNotExists(String email) {
        log.debug("이메일 중복 검사 시작 - 이메일: {}", email);
        if (memberRepository.existsByEmail(email)) {
            log.warn("이메일 중복 발견 - 이메일: {}", email);
            throw new EmailAlreadyExistsException(email);
        }
        log.debug("이메일 중복 검사 통과 - 이메일: {}", email);
    }

    private Member createMemberFromRequest(SignupRequest signUpRequest) {
        log.debug("회원 엔티티 생성 시작 - 이메일: {}", signUpRequest.email());
        String encodedPassword = passwordEncoder.encode(signUpRequest.password());
        log.debug("비밀번호 암호화 완료 - 이메일: {}", signUpRequest.email());

        Member member = Member.builder()
                .email(signUpRequest.email())
                .password(encodedPassword)
                .nickname(signUpRequest.nickname())
                .providerId(signUpRequest.providerId())
                .profileImageUrl(signUpRequest.profileImageUrl())
                .termsAgreed(signUpRequest.termsAgreed())
                .privacyPolicyAgreed(signUpRequest.privacyPolicyAgreed())
                .marketingAgreed(signUpRequest.marketingAgreed())
                .build();

        log.debug("회원 엔티티 생성 완료 - 이메일: {}, 닉네임: {}", member.getEmail(), member.getNickname());
        return member;
    }

    @Transactional(readOnly = true)
    public Member findByEmail(String email) {
        log.debug("이메일로 회원 조회 시작 - 이메일: {}", email);
        try {
            Member member = memberRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("회원을 찾을 수 없음 - 이메일: {}", email);
                        return new UserNotFoundException(email);
                    });
            log.debug("회원 조회 완료 - 이메일: {}, ID: {}", email, member.getId());
            return member;
        } catch (Exception e) {
            log.error("회원 조회 중 오류 발생 - 이메일: {}, 오류: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    @Transactional(readOnly = true)
    public boolean existsByEmail(String email) {
        log.debug("이메일 존재 여부 확인 시작 - 이메일: {}", email);
        boolean exists = memberRepository.existsByEmail(email);
        log.debug("이메일 존재 여부 확인 완료 - 이메일: {}, 존재여부: {}", email, exists);
        return exists;
    }

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String email) {
        log.info("사용자 정보 로딩 시작 - 이메일: {}", email);
        try {
            UserDetails userDetails = memberRepository.findByEmail(email)
                    .orElseThrow(() -> {
                        log.error("사용자를 찾을 수 없음 - 이메일: {}", email);
                        return new UserNotFoundException(email);
                    });
            log.info("사용자 정보 로딩 완료 - 이메일: {}", email);
            return userDetails;
        } catch (Exception e) {
            log.error("사용자 정보 로딩 중 오류 발생 - 이메일: {}, 오류: {}", email, e.getMessage(), e);
            throw e;
        }
    }

    //사용자의 구글 로그인의 완료 여부에 따른 Logic
    public boolean createOrUpdateOAuthMember(
            String email,
            String providerId,
            String accessToken,
            String refreshToken,
            Instant tokenExpirationTime,
            String profileImageUrl) {

        Optional<Member> existingMember = memberRepository.findByEmail(email);

        if (existingMember.isPresent()) {
            Member member = existingMember.get();
            member.updateTokenInfo(
                    accessToken,
                    refreshToken,
                    tokenExpirationTime
            );
            return false;
        } else {
            Member newMember = Member.builder()
                    .email(email)
                    .providerId(providerId)
                    .profileImageUrl(profileImageUrl)
                    .accessToken(accessToken)
                    .refreshToken(refreshToken)
                    .tokenExpirationTime(tokenExpirationTime)
                    .password(null)                    // 나중에 설정
                    .nickname(null)                    // 나중에 설정
                    .termsAgreed(false)               // 나중에 설정
                    .privacyPolicyAgreed(false)       // 나중에 설정
                    .marketingAgreed(false)           // 나중에 설정
                    .build();

            memberRepository.save(newMember);
            return true;
        }
    }

    //구글 로그인 완료시
    public void completeSignUp(
            String email,
            String nickname,
            Boolean termsAgreed,
            Boolean privacyPolicyAgreed,
            Boolean marketingAgreed) {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        member.completeSignUp(nickname, termsAgreed, privacyPolicyAgreed, marketingAgreed);
    }

}