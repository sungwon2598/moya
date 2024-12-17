package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.Category;
import com.study.moya.ai_roadmap.domain.DailyPlan;
import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.domain.WeeklyPlan;
import com.study.moya.ai_roadmap.repository.CategoryRepository;
import com.study.moya.ai_roadmap.repository.DailyPlanRepository;
import com.study.moya.ai_roadmap.repository.RoadMapRepository;
import com.study.moya.ai_roadmap.repository.WeeklyPlanRepository;
import java.util.List;
import java.util.Random;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class DummyDataGeneratorService {

    private final CategoryRepository categoryRepository;
    private final RoadMapRepository roadMapRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final DailyPlanRepository dailyPlanRepository;

    private final Random random = new Random();

    private final String[] topics = {
            "Spring 완벽 가이드", "JPA 마스터 클래스", "Java 성능 최적화",
            "Docker 실전 배포", "MSA 아키텍처 설계", "AWS 클라우드 구축",
            "Kubernetes 운영", "Redis 캐시 전략", "MongoDB 활용",
            "Security 보안 심화"
    };

    private final String[] weeklyKeywords = {
            "기초 개념", "핵심 기능", "아키텍처", "성능 최적화",
            "실전 응용", "트러블슈팅", "모니터링", "배포 전략"
    };

    private final String[] dailyKeywords = {
            "환경설정", "기본기능", "심화학습", "실습과제",
            "프로젝트", "코드리뷰", "발표준비", "회고"
    };

    @Transactional
    public void generateDummyData() {
        log.info("20만개의 데일리 플랜 생성 시작");

        List<Category> leafCategories = categoryRepository.findByDepth(2);
        int roadmapsPerCategory = 3571 / leafCategories.size();
        int dailyPlansCreated = 0;

        for (Category category : leafCategories) {
            log.info("카테고리 '{}' 데이터 생성 중...", category.getName());

            for (int i = 0; i < roadmapsPerCategory && dailyPlansCreated < 200000; i++) {
                // 1. RoadMap 생성
                RoadMap roadMap = RoadMap.builder()
                        .duration(2) // 고정 2개월
                        .goalLevel(random.nextInt(5) + 1)
                        .topic(topics[random.nextInt(topics.length)])
                        .evaluation(generateEvaluation(category.getName()))
                        .category(category)
                        .build();

                RoadMap savedRoadMap = roadMapRepository.save(roadMap);

                // 2. WeeklyPlan 생성 (8주)
                for (int week = 1; week <= 8; week++) {
                    WeeklyPlan weeklyPlan = WeeklyPlan.builder()
                            .weekNumber(week)
                            .keyword(weeklyKeywords[random.nextInt(weeklyKeywords.length)])
                            .roadMap(savedRoadMap)
                            .build();

                    WeeklyPlan savedWeeklyPlan = weeklyPlanRepository.save(weeklyPlan);

                    // 3. DailyPlan 생성 (7일)
                    for (int day = 1; day <= 7 && dailyPlansCreated < 200000; day++) {
                        String dailyKeyword = dailyKeywords[random.nextInt(dailyKeywords.length)];

                        DailyPlan dailyPlan = DailyPlan.builder()
                                .dayNumber(day)
                                .keyword(dailyKeyword)
                                .workSheet(generateWorksheet(roadMap.getTopic(), dailyKeyword))
                                .weeklyPlan(savedWeeklyPlan)
                                .build();

                        dailyPlanRepository.save(dailyPlan);
                        dailyPlansCreated++;
                    }
                }

                if (i > 0 && i % 50 == 0) {
                    log.info("카테고리 '{}': {} 개의 로드맵 생성 완료, 현재 데일리 플랜: {}",
                            category.getName(), i, dailyPlansCreated);
                }
            }

            if (dailyPlansCreated >= 200000) {
                break;
            }
        }

        log.info("더미 데이터 생성 완료: {} 개의 데일리 플랜 생성됨", dailyPlansCreated);
    }

    private String generateEvaluation(String topic) {
        return String.format("""
                # %s 역량 평가 기준
                            
                ## 초급 단계
                - 기본 개념 이해
                - 간단한 예제 구현
                            
                ## 중급 단계
                - 실무 프로젝트 참여
                - 성능 최적화 수행
                            
                ## 고급 단계
                - 아키텍처 설계
                - 기술 선택 결정
                """, topic);
    }

    private String generateWorksheet(String topic, String keyword) {
        return String.format("""
                        # %s - %s 학습 계획
                                    
                        ## 학습 목표
                        - %s 관련 핵심 개념 이해
                        - 실무 적용 방안 학습
                                    
                        ## 세부 계획
                        1. 이론 학습 (2시간)
                           - 공식 문서 학습
                           - 유즈케이스 분석
                                    
                        2. 실습 과제 (3시간)
                           - 예제 코드 구현
                           - 테스트 코드 작성
                                    
                        3. 프로젝트 적용 (2시간)
                           - 실제 프로젝트 적용
                           - 코드 리뷰
                        """,
                topic, keyword, keyword);
    }
}