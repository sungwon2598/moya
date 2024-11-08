package com.study.moya.member.service;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.history.MemberHistory;
import com.study.moya.member.repository.MemberHistoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(propagation = Propagation.REQUIRES_NEW)
@RequiredArgsConstructor
public class MemberHistoryService {

    private final MemberHistoryRepository memberHistoryRepository;

    public void saveHistory(Member member, MemberHistory.HistoryType historyType) {
        MemberHistory history = MemberHistory.from(member, historyType);
        memberHistoryRepository.save(history);
    }
}
