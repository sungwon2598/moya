package com.study.moya.member.domain.history;

import com.study.moya.member.domain.Member;
import com.study.moya.member.service.MemberHistoryService;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEntityListener {

    @Autowired
    @Lazy
    private MemberHistoryService memberHistoryService;

    @PostPersist
    public void postPersist(Member member) {
        memberHistoryService.saveHistory(member, MemberHistory.HistoryType.CREATED);
    }

    @PostUpdate
    public void postUpdate(Member member) {
        MemberHistory.HistoryType historyType = determineHistoryType(member);
        memberHistoryService.saveHistory(member, historyType);
    }

    private MemberHistory.HistoryType determineHistoryType(Member member) {
        switch (member.getStatus()) {
            case ACTIVE:
                return MemberHistory.HistoryType.ACTIVATED;
            case DORMANT:
                return MemberHistory.HistoryType.DORMANT;
            case BLOCKED:
                return MemberHistory.HistoryType.BLOCKED;
            case WITHDRAWN:
                return MemberHistory.HistoryType.WITHDRAWN;
            default:
                return MemberHistory.HistoryType.UPDATED;
        }
    }
}