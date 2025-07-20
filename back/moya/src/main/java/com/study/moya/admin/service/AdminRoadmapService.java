package com.study.moya.admin.service;

import com.study.moya.admin.dto.roadmap.request.UpdateRoadmapInfoRequest;
import com.study.moya.admin.dto.roadmap.response.AdminMemberSubscriptionResponse;
import com.study.moya.ai_roadmap.domain.*;
import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryDTO;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryProjection;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.repository.*;
import com.study.moya.ai_roadmap.service.RoadmapPromptService;
import com.study.moya.ai_roadmap.util.RoadmapResponseParser;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.dto.usage.AiUsageResponse;
import com.study.moya.token.dto.usage.UseTokenRequest;
import com.study.moya.token.service.TokenFacadeService;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminRoadmapService {

    @Value("${openai.api.models.roadmap_generation.model}")
    private String roadmapModel;

    private final OpenAiService openAiService;
    private final RoadmapPromptService promptService;
    private final RoadmapResponseParser responseParser;

    private final RoadMapRepository roadMapRepository;
    private final WeeklyPlanRepository weeklyPlanRepository;
    private final DailyPlanRepository dailyPlanRepository;
    private final TokenFacadeService tokenFacadeService;
    private final CategoryRepository categoryRepository;
    private final MemberRoadMapRepository memberRoadMapRepository;
    private final MemberRepository memberRepository;
    private final EtcRepository etcRepository;

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Long ROADMAP_SERVICE_ID = 1L;
    // etc 타입 상수 정의
    public static final Long MAIN_CATEGORY = 1L;    // 대분류(etc1)
    public static final Long SUB_CATEGORY = 2L;     // 중분류(etc2)

    @Async("taskExecutor")
    public CompletableFuture<WeeklyRoadmapResponse> generateWeeklyRoadmapAsync(RoadmapRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("어드민 - 로드맵 생성 시작");

            String systemPrompt = promptService.createSystemPrompt(request);
            String prompt = promptService.createPrompt(request);

            log.info("어드민 - 생성된 Prompt:\n{}", prompt);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(roadmapModel)
                    .messages(promptService.buildMessages(systemPrompt, prompt))
                    .temperature(0.8)
                    .maxTokens(2000)
                    .build();

            try {
                log.info("어드민 - OpenAI API 호출 시작...");
                ChatCompletionResult chatCompletion = openAiService.createChatCompletion(completionRequest);

                String apiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();
                log.info("어드민 - OpenAI API 응답 수신 완료");

                // 토큰 사용량 로깅 (정보용)
                logTokenUsage(chatCompletion.getUsage());

                // 응답 파싱
                WeeklyRoadmapResponse response = responseParser.parseResponse(apiResponse);

                Category category = null;
                if (request.getMainCategory() != null && !request.getMainCategory().trim().isEmpty()) {
                    category = categoryRepository.findByName(request.getMainCategory())
                            .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + request.getMainCategory()));
                }

                // 파싱된 응답을 엔티티로 저장 (멤버 구독 없이)
                saveCurriculum(Integer.parseInt(request.getCurrentLevel()) + 1,
                        request.getSubCategory(), request.getDuration(),
                        request.getLearningObjective(), response, category,
                        request.getEtc1(), request.getEtc2());

                return response;
            } catch (Exception e) {
                log.error("어드민 - OpenAI API 호출 실패:", e);
                throw new RuntimeException("로드맵 생성 실패", e);
            }
        });
    }

    public Long saveCurriculum(int goalLevel, String topic, int duration, String learningObjective,
                                    WeeklyRoadmapResponse response, Category category,
                                    String etc1Name, String etc2Name) {
        log.info("어드민 - 커리큘럼 저장 시작");

        Etc etc1 = createEtc(etc1Name, MAIN_CATEGORY);
        Etc etc2 = createEtc(etc2Name, SUB_CATEGORY);

        // RoadMap 생성
        RoadMap roadMap = RoadMap.builder()
                .duration(duration)
                .goalLevel(goalLevel)
                .topic(topic)
                .evaluation(response.getCurriculumEvaluation())
                .overallTips(response.getOverallTips())
                .learningObjective(learningObjective)
                .category(category)
                .etc1(etc1)
                .etc2(etc2)
                .build();

        RoadMap savedRoadMap = roadMapRepository.save(roadMap);

        // WeeklyPlan & DailyPlan 생성 (기존과 동일)
        for (WeeklyRoadmapResponse.WeeklyPlan weeklyPlanDto : response.getWeeklyPlans()) {
            WeeklyPlan weeklyPlan = WeeklyPlan.builder()
                    .weekNumber(weeklyPlanDto.getWeek())
                    .keyword(weeklyPlanDto.getWeeklyKeyword())
                    .roadMap(savedRoadMap)
                    .build();

            WeeklyPlan savedWeeklyPlan = weeklyPlanRepository.save(weeklyPlan);

            for (WeeklyRoadmapResponse.DailyPlan dailyPlanDto : weeklyPlanDto.getDailyPlans()) {
                DailyPlan dailyPlan = DailyPlan.builder()
                        .dayNumber(dailyPlanDto.getDay())
                        .keyword(dailyPlanDto.getDailyKeyword())
                        .weeklyPlan(savedWeeklyPlan)
                        .build();

                dailyPlanRepository.save(dailyPlan);
            }
        }

        log.info("어드민 - 커리큘럼 저장 완료. ID: {}", savedRoadMap.getId());
        return savedRoadMap.getId();
    }

    /**
     * 사용자를 로드맵에 구독 처리
     */
    @Transactional
    public MemberRoadMap subscribeToRoadMap(Long memberId, Long roadMapId) {
        log.info("사용자 로드맵 구독 처리 시작. 사용자 ID: {}, 로드맵 ID: {}", memberId, roadMapId);

        // 이미 구독 중인지 확인
        boolean alreadySubscribed = memberRoadMapRepository.existsByMemberIdAndRoadMapId(memberId, roadMapId);
        if (alreadySubscribed) {
            log.info("이미 구독 중인 로드맵입니다. 사용자 ID: {}, 로드맵 ID: {}", memberId, roadMapId);
            return memberRoadMapRepository.findByMemberIdAndRoadMapId(memberId, roadMapId)
                    .orElseThrow(() -> new IllegalStateException("구독 정보가 존재하는데 조회할 수 없습니다."));
        }

        // 사용자 및 로드맵 존재 확인
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("사용자를 찾을 수 없습니다. ID: " + memberId));

        RoadMap roadMap = roadMapRepository.findById(roadMapId)
                .orElseThrow(() -> new EntityNotFoundException("로드맵을 찾을 수 없습니다. ID: " + roadMapId));

        // MemberRoadMap 객체 생성 및 저장
        MemberRoadMap memberRoadMap = MemberRoadMap.builder()
                .member(member)
                .roadMap(roadMap)
                .build();

        MemberRoadMap savedMemberRoadMap = memberRoadMapRepository.save(memberRoadMap);
        log.info("사용자 로드맵 구독 처리 완료. MemberRoadMap ID: {}", savedMemberRoadMap.getId());

        return savedMemberRoadMap;
    }

    /**
     * 사용자의 로드맵 구독 취소
     */
    @Transactional
    public void unsubscribeFromRoadMap(Long memberId, Long roadMapId) {
        log.info("로드맵 구독 취소 시작. 사용자 ID: {}, 로드맵 ID: {}", memberId, roadMapId);

        // 구독 정보 확인
        MemberRoadMap memberRoadMap = memberRoadMapRepository.findByMemberIdAndRoadMapId(memberId, roadMapId)
                .orElseThrow(() -> new EntityNotFoundException("해당 구독 정보를 찾을 수 없습니다."));

        // 구독 삭제
        memberRoadMapRepository.delete(memberRoadMap);
        log.info("로드맵 구독 취소 완료");
    }

    /**
     * 사용자가 구독한 로드맵 요약 정보 조회
     */
    @Transactional(readOnly = true)
    public List<RoadMapSummaryDTO> getMemberRoadMapSummaries(Long memberId) {
        log.info("사용자의 구독 로드맵 요약 정보 조회 시작. 사용자 ID: {}", memberId);

        // Projection으로 조회
        List<RoadMapSummaryProjection> projections = memberRoadMapRepository.findRoadMapSummariesByMemberId(memberId);

        // Projection → DTO 변환
        List<RoadMapSummaryDTO> roadMapSummaries = projections.stream()
                .map(RoadMapSummaryDTO::from)
                .collect(Collectors.toList());

        log.info("사용자의 구독 로드맵 요약 정보 조회 완료. 조회된 로드맵 수: {}", roadMapSummaries.size());
        return roadMapSummaries;
    }

    @Transactional(readOnly = true)
    public WeeklyRoadmapResponse getRoadmapById(Long roadmapId) {
        // 1. 로드맵 정보 조회
        RoadMap roadMap = roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new EntityNotFoundException("해당 로드맵을 찾을 수 없습니다: " + roadmapId));

        // 2. 로드맵에 속한 모든 일별 계획 조회 (주차, 일자 순으로 정렬됨)
        List<DailyPlan> allDailyPlans = dailyPlanRepository.findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(roadmapId);
        log.info("========================데일리 플랜 내용: {}", allDailyPlans.get(1).toString());

        // 3. 주차별로 일별 계획을 그룹화
        Map<Integer, List<DailyPlan>> weeklyGroupedPlans = new HashMap<>();
        for (DailyPlan dailyPlan : allDailyPlans) {
            Integer weekNumber = dailyPlan.getWeeklyPlan().getWeekNumber();
            weeklyGroupedPlans.computeIfAbsent(weekNumber, k -> new ArrayList<>())
                    .add(dailyPlan);
        }

        // 4. 응답 DTO 구성
        List<WeeklyRoadmapResponse.WeeklyPlan> weeklyPlansDto = new ArrayList<>();

        // 주차 번호 오름차순으로 정렬
        List<Integer> sortedWeekNumbers = weeklyGroupedPlans.keySet().stream()
                .sorted()
                .collect(Collectors.toList());

        for (Integer weekNumber : sortedWeekNumbers) {
            List<DailyPlan> weekDailyPlans = weeklyGroupedPlans.get(weekNumber);

            // 각 주차의 첫 번째 DailyPlan에서 WeeklyPlan 정보 가져오기
            WeeklyPlan weeklyPlan = weekDailyPlans.get(0).getWeeklyPlan();

            // 일별 계획 DTO 변환
            List<WeeklyRoadmapResponse.DailyPlan> dailyPlansDto = weekDailyPlans.stream()
                    .map(plan -> new WeeklyRoadmapResponse.DailyPlan(
                            plan.getDayNumber(),
                            plan.getKeyword(),
                            getWorksheet(plan)
                    ))
                    .collect(Collectors.toList());

            // 주차 계획 DTO 생성
            WeeklyRoadmapResponse.WeeklyPlan weeklyPlanDto = new WeeklyRoadmapResponse.WeeklyPlan(
                    weekNumber,
                    weeklyPlan.getKeyword(),
                    dailyPlansDto
            );

            weeklyPlansDto.add(weeklyPlanDto);
        }

        // 5. 최종 응답 생성
        return new WeeklyRoadmapResponse(
                weeklyPlansDto,
                roadMap.getOverallTips(),
                roadMap.getEvaluation(),
                "없음" // 금지된 주제 여부는 별도 필드가 없어 기본값으로 설정
        );
    }

    // ============ 로드맵 수정 기능들 (워크시트 제외) ============

    /**
     * 일별 키워드 수정 (관리자용 - 권한 검증 없음)
     */
    @Transactional
    public String updateDailyKeyword(Long roadmapId, Integer weekNumber, Integer dayNumber, String newKeyword) {
        log.info("어드민 - 일별 키워드 수정 시작 - 로드맵: {}, {}주차 {}일차", roadmapId, weekNumber, dayNumber);

        DailyPlan dailyPlan = dailyPlanRepository.findByRoadmapIdAndWeekAndDay(roadmapId, weekNumber, dayNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("로드맵 %d의 %d주차 %d일차 계획을 찾을 수 없습니다", roadmapId, weekNumber, dayNumber)));

        dailyPlan.updateKeyword(newKeyword);
        dailyPlanRepository.save(dailyPlan);

        log.info("어드민 - 일별 키워드 수정 완료");

        return dailyPlan.getKeyword();
    }

    /**
     * 주차별 키워드 수정 (관리자용)
     */
    @Transactional
    public String updateWeeklyKeyword(Long roadmapId, Integer weekNumber, String newKeyword) {
        log.info("어드민 - 주차별 키워드 수정 시작 - 로드맵: {}, {}주차", roadmapId, weekNumber);

        WeeklyPlan weeklyPlan = weeklyPlanRepository.findByRoadmapIdAndWeek(roadmapId, weekNumber)
                .orElseThrow(() -> new EntityNotFoundException(
                        String.format("로드맵 %d의 %d주차 계획을 찾을 수 없습니다", roadmapId, weekNumber)));

        weeklyPlan.updateKeyword(newKeyword);
        weeklyPlanRepository.save(weeklyPlan);

        log.info("어드민 - 주차별 키워드 수정 완료");
        return weeklyPlan.getKeyword(); // 수정된 키워드 반환
    }

    /**
     * 전체 팁 수정 (관리자용)
     */
    @Transactional
    public List<String> updateOverallTips(Long roadmapId, List<String> newTips) {
        log.info("어드민 - 전체 팁 수정 시작 - 로드맵: {}", roadmapId);

        RoadMap roadMap = getRoadmap(roadmapId);
        roadMap.updateOverallTips(newTips);
        roadMapRepository.save(roadMap);

        log.info("어드민 - 전체 팁 수정 완료");
        return roadMap.getOverallTips(); // 수정된 팁 목록 반환
    }

    /**
     * 로드맵 기본 정보 수정 (관리자용)
     */
    @Transactional
    public Map<String, Object> updateRoadmapInfo(Long roadmapId, UpdateRoadmapInfoRequest request) {
        log.info("어드민 - 로드맵 기본 정보 수정 시작 - 로드맵: {}", roadmapId);

        RoadMap roadMap = getRoadmap(roadmapId);
        roadMap.updateBasicInfo(
                request.getTopic(),
                request.getDuration(),
                request.getLearningObjective(),
                request.getEvaluation()
        );

        roadMapRepository.save(roadMap);

        log.info("어드민 - 로드맵 기본 정보 수정 완료");

        // 수정된 정보를 Map으로 반환
        Map<String, Object> updatedInfo = new HashMap<>();
        updatedInfo.put("topic", roadMap.getTopic());
        updatedInfo.put("duration", roadMap.getDuration());
        updatedInfo.put("learningObjective", roadMap.getLearningObjective());
        updatedInfo.put("evaluation", roadMap.getEvaluation());

        return updatedInfo;
    }

    /**
     * 로드맵 삭제 (관리자용)
     */
    @Transactional
    public void deleteRoadmap(Long roadmapId) {
        log.info("어드민 - 로드맵 삭제 시작 - 로드맵: {}", roadmapId);

        // 순차적 삭제 (구독자 수 조회 제거)
        dailyPlanRepository.deleteByWeeklyPlanRoadMapId(roadmapId);
        weeklyPlanRepository.deleteByRoadMapId(roadmapId);
        memberRoadMapRepository.deleteByRoadMapId(roadmapId);
        roadMapRepository.deleteById(roadmapId);

        log.info("어드민 - 로드맵 삭제 완료");
    }

    /**
     * 전체 로드맵 조회 (관리자용)
     */
    @Transactional(readOnly = true)
    public List<RoadMapSummaryDTO> getAllRoadmaps() {
        log.info("어드민 - 전체 로드맵 조회");

        return roadMapRepository.findAllWithRelations().stream()
                .map(roadmap -> new RoadMapSummaryDTO(
                        roadmap.getId(),
                        roadmap.getCategory() != null ? roadmap.getCategory().getName() :
                                (roadmap.getEtc1() != null ? roadmap.getEtc1().getName() : "미분류"),
                        roadmap.getTopic(),
                        roadmap.getDuration()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminMemberSubscriptionResponse> getMemberSubscriptions(Long memberId) {
        log.info("어드민 - 멤버 구독 로드맵 조회 - 멤버 ID: {}", memberId);

        // 멤버 존재 여부 확인
        memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("멤버를 찾을 수 없습니다: " + memberId));

        // 기존 메서드 그대로 사용!
        return memberRoadMapRepository.findRoadMapSummariesByMemberId(memberId).stream()
                .map(projection -> new AdminMemberSubscriptionResponse(
                        projection.getId(),
                        projection.getMainCategory(),
                        projection.getSubCategory(),
                        projection.getDuration()
                ))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public List<AdminMemberSubscriptionResponse> getRoadMapsByCategory(Long categoryId) {
        log.info("어드민 - 카테고리별 로드맵 조회 - 카테고리 ID: {}", categoryId);

        // 카테고리 존재 여부 확인 (선택사항)
        // categoryRepository.findById(categoryId)
        //         .orElseThrow(() -> new EntityNotFoundException("카테고리를 찾을 수 없습니다: " + categoryId));

        return roadMapRepository.findAdminRoadMapsByCategoryId(categoryId).stream()
                .map(projection -> new AdminMemberSubscriptionResponse(
                        projection.getId(),
                        projection.getMainCategory(),
                        projection.getSubCategory(),
                        projection.getDuration()
                ))
                .collect(Collectors.toList());
    }

    // ============ 유틸리티 메서드들 ============

    /**
     * 로드맵 조회 (공통)
     */
    private RoadMap getRoadmap(Long roadmapId) {
        return roadMapRepository.findById(roadmapId)
                .orElseThrow(() -> new EntityNotFoundException("해당 로드맵을 찾을 수 없습니다: " + roadmapId));
    }

    private String getWorksheet(DailyPlan plan) {
        if (plan.getWorkSheet() == null) {
            return "";
        }
        return plan.getWorkSheet();
    }

    private Etc createEtc(String name, Long etcType){
        if (name == null || name.trim().isEmpty()) {
            return null;
        }

        Etc etc = Etc.builder()
                .name(name)
                .etcType(etcType)
                .build();

        return etcRepository.save(etc);
    }

    private void logTokenUsage(Usage usage) {
        if (usage != null) {
            log.info("OpenAI 토큰 사용량 - Prompt Tokens: {}, Completion Tokens: {}, Total Tokens: {}",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens());
        } else {
            log.warn("OpenAI 토큰 사용량 정보를 가져올 수 없습니다.");
        }
    }
}