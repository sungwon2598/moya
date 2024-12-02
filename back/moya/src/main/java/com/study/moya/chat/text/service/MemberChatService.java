package com.study.moya.chat.text.service;

import com.study.moya.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MemberChatService {

    private final MemberRepository memberRepository;

}
