package com.study.moya.member.event;


public record MemberRegisteredEvent(
        Long memberId, String email, String nickname
) {}