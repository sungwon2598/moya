package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "worksheets")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Worksheet extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Column(name = "daily_plan_id", nullable = false, unique = true)
    private Long dailyPlanId;

    @Builder
    public Worksheet(String content, Long dailyPlanId) {
        this.content = content;
        this.dailyPlanId = dailyPlanId;
    }
}