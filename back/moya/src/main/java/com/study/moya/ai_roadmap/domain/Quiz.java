package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "quizzes")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Quiz extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 500)
    private String question;

    @Column(nullable = false, length = 500)
    private String answer;

    @Column(name = "daily_plan_id", nullable = false)
    private Long dailyPlanId;

    @Builder
    public Quiz(String question, String answer, Long dailyPlanId) {
        this.question = question;
        this.answer = answer;
        this.dailyPlanId = dailyPlanId;
    }
}