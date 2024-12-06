package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Quiz extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "daily_roadmap_id")
    private DailyRoadmap dailyRoadmap;

    @Column(columnDefinition = "LONGTEXT")
    private String question;

    @Column(columnDefinition = "LONGTEXT")
    private String answer;

    @Column(columnDefinition = "LONGTEXT")
    private String userAnswer;

    @Builder
    public Quiz(String question, String answer, DailyRoadmap dailyRoadmap) {
        this.question = question;
        this.answer = answer;
        this.dailyRoadmap = dailyRoadmap;
    }

    public void submitAnswer(String userAnswer) {
        this.userAnswer = userAnswer;
    }
}