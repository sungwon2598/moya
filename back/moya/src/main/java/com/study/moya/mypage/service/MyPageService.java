package com.study.moya.mypage.service;

import com.study.moya.error.constants.MemberErrorCode;
import com.study.moya.error.exception.MemberException;
import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.mypage.dto.MyPageResponse;
import com.study.moya.mypage.dto.MyPageUpdateRequest;
import com.study.moya.mypage.exception.MyPageErrorCode;
import com.study.moya.mypage.exception.MyPageException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MemberRepository memberRepository;

    public MyPageResponse getMyPageInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MyPageException.of(MyPageErrorCode.MEMBER_NOT_FOUND));
        return MyPageResponse.from(member);
    }

    @Transactional
    public MyPageResponse updateMyPage(Long memberId, MyPageUpdateRequest request) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MyPageException.of(MyPageErrorCode.MEMBER_NOT_FOUND));

        if(!member.getNickname().equals(request.getNickname()) &&
                memberRepository.existsByNicknameAndStatusNot(
                        request.getNickname(),
                        MemberStatus.WITHDRAWN
                )){
            throw MyPageException.of(MyPageErrorCode.DUPLICATE_NICKNAME);
        }
        member.updateNickname(request.getNickname());
        member.updateProfileImage(request.getProfileImageUrl());
        member.updateIntroduction(request.getIntroduction());

        return MyPageResponse.from(member);
    }
}
