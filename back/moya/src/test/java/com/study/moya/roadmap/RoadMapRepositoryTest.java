//package com.study.moya.roadmap;
//
//import com.study.moya.ai_roadmap.domain.Category;
//import com.study.moya.ai_roadmap.domain.DailyPlan;
//import com.study.moya.ai_roadmap.domain.RoadMap;
//import com.study.moya.ai_roadmap.domain.WeeklyPlan;
//import com.study.moya.ai_roadmap.repository.CategoryRepository;
//import com.study.moya.ai_roadmap.repository.DailyPlanRepository;
//import com.study.moya.ai_roadmap.repository.RoadMapRepository;
//import com.study.moya.ai_roadmap.repository.WeeklyPlanRepository;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.Random;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//public class RoadMapRepositoryTest {
//
//    @Autowired
//    private CategoryRepository categoryRepository;
//
//    @Autowired
//    private RoadMapRepository roadMapRepository;
//
//    @Autowired
//    private WeeklyPlanRepository weeklyPlanRepository;
//
//    @Autowired
//    private DailyPlanRepository dailyPlanRepository;
//
//    private final Random random = new Random();
//
//    private final Map<String, String[]> categoryHierarchy = new HashMap<>() {{
//        // 개발 분야
//        put("개발", new String[]{"백엔드", "프론트엔드", "모바일", "데브옵스", "데이터"});
//        // 백엔드 세부
//        put("백엔드", new String[]{"Java", "Python", "Node.js", "Go", "C#"});
//        put("프론트엔드", new String[]{"React", "Vue", "Angular", "JavaScript", "TypeScript"});
//        put("모바일", new String[]{"Android", "iOS", "React Native", "Flutter", "Unity"});
//        put("데브옵스", new String[]{"Docker", "Kubernetes", "AWS", "CI/CD", "모니터링"});
//        put("데이터", new String[]{"BigData", "Machine Learning", "데이터 분석", "데이터 엔지니어링", "AI"});
//    }};
//
//    private final String[] topics = {
//            "입문에서 실전까지", "심화 과정", "마스터 클래스", "전문가 과정",
//            "실무 프로젝트", "아키텍처 설계", "성능 최적화", "보안 강화",
//            "테스트 자동화", "운영 관리", "모니터링 구축", "마이그레이션"
//    };
//
//    private final String[] weeklyKeywords = {
//            "기초 이론", "핵심 개념", "실전 활용", "아키텍처", "성능 개선",
//            "보안 설정", "테스트 전략", "운영 환경", "모니터링", "트러블슈팅",
//            "코드 리뷰", "문서화", "배포 전략", "스케일링", "유지보수"
//    };
//
//    private final String[] dailyKeywords = {
//            "이론 학습", "실습 과제", "프로젝트", "코드 리뷰", "문서화",
//            "성능 측정", "테스트 작성", "리팩토링", "발표 준비", "회고",
//            "케이스 스터디", "페어 프로그래밍", "코드 분석", "디버깅", "최적화"
//    };
//
//    @Test
//    @Transactional
//    void generateTestData() {
//        // 계층형 카테고리 생성
//        List<Category> allCategories = createCategoryHierarchy();
//        categoryRepository.saveAll(allCategories);
//
//        // 최하위 카테고리만 선택 (depth가 2인 카테고리)
//        List<Category> leafCategories = allCategories.stream()
//                .filter(c -> c.getDepth() == 2)
//                .toList();
//
//        List<RoadMap> allRoadMaps = new ArrayList<>();
//        List<WeeklyPlan> allWeeklyPlans = new ArrayList<>();
//        List<DailyPlan> allDailyPlans = new ArrayList<>();
//
//        // 각 최하위 카테고리별로 로드맵 생성
//        for (Category category : leafCategories) {
//            List<RoadMap> roadMaps = createRoadMaps(category, 20); // 카테고리당 20개의 로드맵
//            allRoadMaps.addAll(roadMaps);
//
//            for (RoadMap roadMap : roadMaps) {
//                List<WeeklyPlan> weeklyPlans = createWeeklyPlans(roadMap);
//                allWeeklyPlans.addAll(weeklyPlans);
//
//                for (WeeklyPlan weeklyPlan : weeklyPlans) {
//                    List<DailyPlan> dailyPlans = createDailyPlans(weeklyPlan);
//                    allDailyPlans.addAll(dailyPlans);
//                }
//            }
//        }
//
//        // 벌크 저장
//        roadMapRepository.saveAll(allRoadMaps);
//        weeklyPlanRepository.saveAll(allWeeklyPlans);
//        dailyPlanRepository.saveAll(allDailyPlans);
//    }
//
//    private List<Category> createCategoryHierarchy() {
//        List<Category> allCategories = new ArrayList<>();
//
//        // 루트 카테고리 생성
//        Category root = Category.builder()
//                .name("개발")
//                .build();
//        allCategories.add(root);
//
//        // 중간 카테고리 생성
//        String[] mainCategories = categoryHierarchy.get("개발");
//        for (String mainCategoryName : mainCategories) {
//            Category mainCategory = Category.builder()
//                    .name(mainCategoryName)
//                    .parent(root)
//                    .build();
//            allCategories.add(mainCategory);
//
//            // 최하위 카테고리 생성
//            String[] subCategories = categoryHierarchy.get(mainCategoryName);
//            if (subCategories != null) {
//                for (String subCategoryName : subCategories) {
//                    Category subCategory = Category.builder()
//                            .name(subCategoryName)
//                            .parent(mainCategory)
//                            .build();
//                    allCategories.add(subCategory);
//                }
//            }
//        }
//
//        return allCategories;
//    }
//
//    private List<RoadMap> createRoadMaps(Category category, int count) {
//        List<RoadMap> roadMaps = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            List<String> tips = generateTips(category);
//            RoadMap roadMap = RoadMap.builder()
//                    .topic(category.getName() + " " + topics[random.nextInt(topics.length)])
//                    .goalLevel(random.nextInt(5) + 1)
//                    .duration(random.nextInt(24) + 1) // 1-24개월
//                    .evaluation(generateEvaluation(category))
//                    .overallTips(tips)
//                    .category(category)
//                    .build();
//            roadMaps.add(roadMap);
//        }
//        return roadMaps;
//    }
//
//    private List<String> generateTips(Category category) {
//        List<String> tips = new ArrayList<>();
//        int tipCount = random.nextInt(3) + 3; // 3-5개의 팁
//
//        for (int i = 0; i < tipCount; i++) {
//            tips.add(String.format("%s 학습 팁 #%d: %s",
//                    category.getName(),
//                    (i + 1),
//                    generateTipContent(category)));
//        }
//        return tips;
//    }
//
//    private String generateTipContent(Category category) {
//        return String.format("매일 %s 관련 실습을 진행하고 깃헙에 커밋하세요.", category.getName());
//    }
//
//    private String generateEvaluation(Category category) {
//        return String.format("""
//                        # %s 역량 평가 기준
//
//                        ## 기초 단계 (1-2레벨)
//                        - 기본 문법과 개념 이해
//                        - 간단한 예제 구현 가능
//
//                        ## 중급 단계 (3-4레벨)
//                        - 실무 프로젝트 구현 가능
//                        - 성능 최적화 가능
//
//                        ## 고급 단계 (5레벨)
//                        - 아키텍처 설계 가능
//                        - 다른 개발자 멘토링 가능
//                        """,
//                category.getName());
//    }
//
//    private List<WeeklyPlan> createWeeklyPlans(RoadMap roadMap) {
//        List<WeeklyPlan> weeklyPlans = new ArrayList<>();
//        int weeks = roadMap.getDuration() * 4; // 한 달을 4주로 계산
//
//        for (int week = 1; week <= weeks; week++) {
//            WeeklyPlan weeklyPlan = WeeklyPlan.builder()
//                    .weekNumber(week)
//                    .keyword(weeklyKeywords[random.nextInt(weeklyKeywords.length)])
//                    .roadMap(roadMap)
//                    .build();
//            weeklyPlans.add(weeklyPlan);
//        }
//        return weeklyPlans;
//    }
//
//    private List<DailyPlan> createDailyPlans(WeeklyPlan weeklyPlan) {
//        List<DailyPlan> dailyPlans = new ArrayList<>();
//        for (int day = 1; day <= 7; day++) {
//            String keyword = dailyKeywords[random.nextInt(dailyKeywords.length)];
//            String workSheet = random.nextBoolean() ?
//                    generateWorkSheet(weeklyPlan.getRoadMap().getCategory(), keyword) : null;
//
//            DailyPlan dailyPlan = DailyPlan.builder()
//                    .dayNumber(day)
//                    .keyword(keyword)
//                    .workSheet(workSheet)
//                    .weeklyPlan(weeklyPlan)
//                    .build();
//            dailyPlans.add(dailyPlan);
//        }
//        return dailyPlans;
//    }
//
//    private String generateWorkSheet(Category category, String keyword) {
//        return String.format("""
//                        # %s - %s 학습 계획
//
//                        ## 목표
//                        - %s 분야의 %s 역량 향상
//                        - 실무 적용 가능한 수준의 숙달
//
//                        ## 세부 계획
//                        1. 이론 학습 (2시간)
//                            - 공식 문서 정독
//                            - 베스트 프랙티스 조사
//
//                        2. 실습 진행 (3시간)
//                            - 예제 코드 구현
//                            - 테스트 코드 작성
//
//                        3. 프로젝트 적용 (2시간)
//                            - 실제 프로젝트에 적용
//                            - 코드 리뷰 및 피드백
//
//                        ## 체크포인트
//                        - [ ] 핵심 개념 이해 완료
//                        - [ ] 예제 구현 완료
//                        - [ ] 테스트 코드 작성 완료
//                        - [ ] 실제 프로젝트 적용 완료
//                        """,
//                category.getName(), keyword,
//                category.getName(), keyword
//        );
//    }
//}