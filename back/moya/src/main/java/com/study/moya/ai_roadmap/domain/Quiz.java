package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "daily_plan_id")
    private DailyPlan dailyPlan;

    @Builder
    public Quiz(String question, String answer, DailyPlan dailyPlan) {
        validateQuiz(question, answer);
        this.question = question;
        this.answer = answer;
        this.dailyPlan = dailyPlan;
    }

    private void validateQuiz(String question, String answer) {
        if (question == null || question.isBlank()) {
            throw new IllegalArgumentException("문제는 필수입니다.");
        }
        if (answer == null || answer.isBlank()) {
            throw new IllegalArgumentException("답변은 필수입니다.");
        }
    }
}