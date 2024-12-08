package com.study.moya.roadmap;

import static org.assertj.core.api.Assertions.assertThat;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.repository.RoadMapRepository;
import com.study.moya.ai_roadmap.service.RoadmapService;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.stat.Statistics;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@ActiveProfiles("test")
public class RoadmapServicePerformanceTest {

    @Autowired
    private RoadmapService roadmapService;

    @Autowired
    private RoadMapRepository roadMapRepository;

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private ObjectMapper objectMapper;

    private WeeklyRoadmapResponse createRealTestData() {
        String jsonData = """
                {
                   "weeklyPlans": [
                     {
                       "week": 1,
                       "weeklyKeyword": "자바 기초 이해",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "자바 설치 및 환경 설정"},
                         {"day": 2, "dailyKeyword": "자바의 기본 구조 이해"},
                         {"day": 3, "dailyKeyword": "변수와 데이터 타입"},
                         {"day": 4, "dailyKeyword": "연산자와 표현식"},
                         {"day": 5, "dailyKeyword": "제어문 기본"},
                         {"day": 6, "dailyKeyword": "자바 클래스 소개"},
                         {"day": 7, "dailyKeyword": "자바 메서드 기본"}
                       ]
                     },
                     {
                       "week": 2,
                       "weeklyKeyword": "객체 지향 프로그래밍 기본",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "클래스와 객체 개념"},
                         {"day": 2, "dailyKeyword": "메서드와 생성자"},
                         {"day": 3, "dailyKeyword": "상속의 기본"},
                         {"day": 4, "dailyKeyword": "다형성 이해"},
                         {"day": 5, "dailyKeyword": "추상 클래스와 인터페이스 소개"},
                         {"day": 6, "dailyKeyword": "패키지와 접근 제어자"},
                         {"day": 7, "dailyKeyword": "자바 API 활용"}
                       ]
                     },
                     {
                       "week": 3,
                       "weeklyKeyword": "기본 자료 구조 이해",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "배열과 리스트"},
                         {"day": 2, "dailyKeyword": "맵과 세트"},
                         {"day": 3, "dailyKeyword": "컬렉션 프레임워크"},
                         {"day": 4, "dailyKeyword": "제네릭스 기본"},
                         {"day": 5, "dailyKeyword": "컬렉션 클래스 활용"},
                         {"day": 6, "dailyKeyword": "반복자 사용법"},
                         {"day": 7, "dailyKeyword": "컬렉션 활용 예제"}
                       ]
                     },
                     {
                       "week": 4,
                       "weeklyKeyword": "예외 처리 및 파일 입출력",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "예외 처리 개념"},
                         {"day": 2, "dailyKeyword": "try-catch-finally 구조"},
                         {"day": 3, "dailyKeyword": "사용자 정의 예외"},
                         {"day": 4, "dailyKeyword": "파일 읽기"},
                         {"day": 5, "dailyKeyword": "파일 쓰기"},
                         {"day": 6, "dailyKeyword": "파일 입출력 예제"},
                         {"day": 7, "dailyKeyword": "예외 처리 및 입출력 복습"}
                       ]
                     },
                     {
                       "week": 5,
                       "weeklyKeyword": "자바의 스레드 프로그래밍",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "스레드 개념"},
                         {"day": 2, "dailyKeyword": "스레드 생성 및 실행"},
                         {"day": 3, "dailyKeyword": "스레드의 상태"},
                         {"day": 4, "dailyKeyword": "동기화와 Lock"},
                         {"day": 5, "dailyKeyword": "스레드 간 통신"},
                         {"day": 6, "dailyKeyword": "스레드 활용 예제"},
                         {"day": 7, "dailyKeyword": "스레드 프로그래밍 복습"}
                       ]
                     },
                     {
                       "week": 6,
                       "weeklyKeyword": "자바 네트워크 프로그래밍",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "네트워크 기본 개념"},
                         {"day": 2, "dailyKeyword": "소켓 프로그래밍 기초"},
                         {"day": 3, "dailyKeyword": "TCP/UDP 프로토콜"},
                         {"day": 4, "dailyKeyword": "서버 소켓 프로그래밍"},
                         {"day": 5, "dailyKeyword": "클라이언트 소켓 프로그래밍"},
                         {"day": 6, "dailyKeyword": "네트워크 예제 구현"},
                         {"day": 7, "dailyKeyword": "네트워크 프로그래밍 복습"}
                       ]
                     },
                     {
                       "week": 7,
                       "weeklyKeyword": "자바 GUI 프로그래밍",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "GUI 기본 개념"},
                         {"day": 2, "dailyKeyword": "스윙 컴포넌트 사용"},
                         {"day": 3, "dailyKeyword": "이벤트 처리"},
                         {"day": 4, "dailyKeyword": "레이아웃 관리자"},
                         {"day": 5, "dailyKeyword": "GUI 애플리케이션 설계"},
                         {"day": 6, "dailyKeyword": "GUI 예제 구현"},
                         {"day": 7, "dailyKeyword": "GUI 프로그래밍 복습"}
                       ]
                     },
                     {
                       "week": 8,
                       "weeklyKeyword": "자바 프로젝트 실습",
                       "dailyPlans": [
                         {"day": 1, "dailyKeyword": "프로젝트 주제 선정"},
                         {"day": 2, "dailyKeyword": "프로젝트 설계"},
                         {"day": 3, "dailyKeyword": "기본 코드 작성"},
                         {"day": 4, "dailyKeyword": "기능 구현"},
                         {"day": 5, "dailyKeyword": "테스트 및 디버깅"},
                         {"day": 6, "dailyKeyword": "프로젝트 개선"},
                         {"day": 7, "dailyKeyword": "프로젝트 최종 검토"}
                       ]
                     }
                   ],
                   "overallTips": ["일일 학습 목표를 명확하게 설정하고 집중하세요.", "자바의 기초를 확실히 이해한 후 실습을 통해 경험을 쌓으세요.", "주기적으로 복습하여 학습 내용을 정리하세요."],
                   "curriculumEvaluation": "초급에서 중급 수준으로의 적절한 커리큘럼입니다. 기초 개념부터 실습까지 체계적으로 학습할 수 있습니다.",
                   "hasRestrictedTopics": "없음"
                 }
                """;
        try {
            return objectMapper.readValue(jsonData, WeeklyRoadmapResponse.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse test data", e);
        }
    }

    @Test
    @DisplayName("실제 데이터로 커리큘럼 저장 성능 테스트")
    @Transactional
    void realDataInsertPerformanceTest() {
        // Given
        WeeklyRoadmapResponse response = createRealTestData();
        Session session = entityManager.unwrap(Session.class);
        SessionFactory sessionFactory = session.getSessionFactory();
        Statistics statistics = sessionFactory.getStatistics();
        statistics.setStatisticsEnabled(true);
        statistics.clear();

        // When
        long startTime = System.nanoTime();
        Long savedId = roadmapService.saveCurriculum("자바", response);
        entityManager.flush();
        entityManager.clear();
        long endTime = System.nanoTime();

        // Then
        double durationInMillis = (endTime - startTime) / 1_000_000.0;
        long insertCount = statistics.getEntityInsertCount();

        System.out.println("====== 성능 테스트 결과 ======");
        System.out.println("총 소요 시간: " + durationInMillis + "ms");
        System.out.println("총 Insert 쿼리 수: " + insertCount);
        System.out.println("쿼리 당 평균 시간: " + (durationInMillis / insertCount) + "ms");

        // 엔티티 수 검증
        RoadMap savedCurriculum = roadMapRepository.findById(savedId).orElseThrow();
        assertThat(savedCurriculum.getWeeklyPlans()).hasSize(8); // 8주
        assertThat(savedCurriculum.getWeeklyPlans()
                .stream()
                .mapToInt(wp -> wp.getDailyPlans().size())
                .sum()).isEqualTo(56); // 8주 * 7일 = 56일
        assertThat(savedCurriculum.getOverallTips()).hasSize(3); // 3개의 팁
    }
}