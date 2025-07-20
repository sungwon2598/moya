package com.study.moya.token.domain.ticket;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.enums.AiServiceType;
import com.study.moya.token.domain.enums.AiUsageStatus;
import com.study.moya.token.domain.enums.TicketType;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(name = "ticket_usages")
public class TicketUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Enumerated(EnumType.STRING)
    private AiServiceType serviceType;    // 기존 enum 재사용

    @Enumerated(EnumType.STRING)
    private TicketType ticketType;        // 새로 추가한 enum

    @Enumerated(EnumType.STRING)
    private AiUsageStatus status;         // 기존 enum 재사용

    public void completeUsage() {
        this.status = AiUsageStatus.COMPLETED;
    }

    public void failUsage() {
        this.status = AiUsageStatus.FAILED;
    }
}
