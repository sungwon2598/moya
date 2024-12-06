package com.study.moya.ai_roadmap.domain;

import com.study.moya.BaseEntity;
import jakarta.persistence.*;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class DailyRoadmap extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "weekly_roadmap_id")
    private WeeklyRoadmap weeklyRoadmap;

    @Column(columnDefinition = "LONGTEXT")
    private String studyGuide;  // 학습 가이드

    private String keyword; //오늘 학습할 키워드 (변수, const와 let 등)

    private boolean isCompleted;

    private int dayNumber;

    @Builder
    public DailyRoadmap(int dayNumber, String keyword, WeeklyRoadmap weeklyRoadmaps) {
        this.dayNumber = dayNumber;
        this.keyword = keyword;
        this.weeklyRoadmap = weeklyRoadmaps;
        this.isCompleted = false;
    }

    public void addStudyGuide(String studyGuide){
        this.studyGuide = studyGuide;
    }
}
