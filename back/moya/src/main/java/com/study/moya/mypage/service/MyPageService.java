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
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.service.ticket.TicketFacadeService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class MyPageService {
    private final MemberRepository memberRepository;
    private final TicketFacadeService ticketFacadeService;

    @Transactional  // 티켓 계정 생성을 위해 쓰기 가능한 트랜잭션으로 변경
    public MyPageResponse getMyPageInfo(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> MyPageException.of(MyPageErrorCode.MEMBER_NOT_FOUND));
        
        // 티켓 계정이 없으면 자동으로 생성
        ticketFacadeService.getOrCreateTicketAccount(memberId);
        
        // 각 티켓 타입별 잔액 조회
        Long roadmapTicketBalance = ticketFacadeService.getTicketBalance(memberId, TicketType.ROADMAP_TICKET);
        Long worksheetTicketBalance = ticketFacadeService.getTicketBalance(memberId, TicketType.WORKSHEET_TICKET);
        
        return MyPageResponse.from(member, roadmapTicketBalance, worksheetTicketBalance);
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
