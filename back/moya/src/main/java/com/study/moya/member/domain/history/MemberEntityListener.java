package com.study.moya.member.domain.history;

import com.study.moya.member.domain.Member;
import com.study.moya.member.domain.MemberStatus;
import com.study.moya.member.domain.PrivacyConsent;
import com.study.moya.member.domain.history.MemberHistory.HistoryType;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostUpdate;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MemberEntityListener {

    @PersistenceContext
    private EntityManager entityManager;

    @PostPersist
    public void onPostPersist(Member member) {
        createHistory(member, HistoryType.CREATED);
    }

    @PostUpdate
    public void onPostUpdate(Member member) {
        HistoryType historyType = determineHistoryType(member);
        createHistory(member, historyType);
    }

    private void createHistory(Member member, HistoryType historyType) {
        MemberHistory history = MemberHistory.from(member, historyType);
        entityManager.persist(history);
    }

    private HistoryType determineHistoryType(Member member) {
        EntityManager em = entityManager.getEntityManagerFactory().createEntityManager();
        try {
            Member originalMember = em.find(Member.class, member.getId());

            if (originalMember == null) {
                return HistoryType.UPDATED;
            }

            if (isStatusChanged(originalMember, member)) {
                return getStatusChangeType(member.getStatus());
            }

            if (isProfileChanged(originalMember, member)) {
                return HistoryType.PROFILE_UPDATED;
            }

            if (isEmailChanged(originalMember, member)) {
                return HistoryType.EMAIL_UPDATED;
            }

            if (isPrivacyConsentChanged(originalMember, member)) {
                return HistoryType.PRIVACY_UPDATED;
            }

//            if (isPointAccountChanged(originalMember, member)) {
//                return HistoryType.POINT_UPDATED;
//            }

            return HistoryType.UPDATED;
        } finally {
            em.close();
        }
    }

    private boolean isStatusChanged(Member originalMember, Member currentMember) {
        return originalMember != null &&
                currentMember != null &&
                !Objects.equals(originalMember.getStatus(), currentMember.getStatus());
    }

    private boolean isProfileChanged(Member originalMember, Member currentMember) {
        return originalMember != null &&
                currentMember != null &&
                (!Objects.equals(originalMember.getNickname(), currentMember.getNickname()) ||
                        !Objects.equals(originalMember.getProfileImageUrl(), currentMember.getProfileImageUrl()));
    }

    private boolean isPrivacyConsentChanged(Member originalMember, Member currentMember) {
        if (originalMember == null || currentMember == null) {
            return false;
        }

        PrivacyConsent originalConsent = originalMember.getPrivacyConsent();
        PrivacyConsent currentConsent = currentMember.getPrivacyConsent();

        if (originalConsent == null || currentConsent == null) {
            return originalConsent != currentConsent;
        }

        return !Objects.equals(originalConsent.getMarketingAgreed(), currentConsent.getMarketingAgreed()) ||
                !Objects.equals(originalConsent.getPrivacyPolicyAgreed(), currentConsent.getPrivacyPolicyAgreed()) ||
                !Objects.equals(originalConsent.getTermsAgreed(), currentConsent.getTermsAgreed());
    }

    private boolean isEmailChanged(Member originalMember, Member currentMember) {
        return originalMember != null &&
                currentMember != null &&
                !Objects.equals(originalMember.getEmail(), currentMember.getEmail());
    }

//    private boolean isPointAccountChanged(Member originalMember, Member currentMember) {
//        if (originalMember == null || currentMember == null) {
//            return false;
//        }
//
//        Integer originalBalance = originalMember.getPointAccount() != null ?
//                originalMember.getPointAccount().getBalance() : 0;
//        Integer currentBalance = currentMember.getPointAccount() != null ?
//                currentMember.getPointAccount().getBalance() : 0;
//
//        return !Objects.equals(originalBalance, currentBalance);
//    }

    private HistoryType getStatusChangeType(MemberStatus status) {
        if (status == null) return HistoryType.UPDATED;

        return switch (status) {
            case WITHDRAWN -> HistoryType.WITHDRAWN;
            case DORMANT -> HistoryType.DORMANT;
            case BLOCKED -> HistoryType.BLOCKED;
            case ACTIVE -> HistoryType.ACTIVATED;
        };
    }
}