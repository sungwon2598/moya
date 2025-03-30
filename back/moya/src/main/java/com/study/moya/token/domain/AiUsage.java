package com.study.moya.token.domain;

import com.study.moya.BaseEntity;
import com.study.moya.member.domain.Member;
import com.study.moya.token.domain.enums.AiUsageStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "ai_usages")
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AiUsage extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @ManyToOne
    @JoinColumn(name = "service_id", nullable = false)
    private AiService aiService;

    @Column(columnDefinition = "TEXT")
    private String requestData;

    private Long tokenCost;

    private Long actualTokenUsage;

    @Enumerated(EnumType.STRING)
    private AiUsageStatus status;

    public void completeUsage() {
        this.status = AiUsageStatus.COMPLETED;
    }

    public void failUsage() {
        this.status = AiUsageStatus.FAILED;
    }

    public void recordActualTokenUsage(Long actualTokenUsage) {
        this.actualTokenUsage = actualTokenUsage;
    }

}