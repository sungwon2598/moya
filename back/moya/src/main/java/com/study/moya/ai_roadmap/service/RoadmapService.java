package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.constants.LearningObjective;
import com.study.moya.ai_roadmap.domain.*;
import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.response.RoadMapSimpleDto;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryDTO;
import com.study.moya.ai_roadmap.dto.response.RoadMapSummaryProjection;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.repository.*;
import com.study.moya.ai_roadmap.util.RoadmapResponseParser;
import com.study.moya.member.domain.Member;
import com.study.moya.member.repository.MemberRepository;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.dto.ticket.reponse.TicketUsageResponse;
import com.study.moya.token.dto.usage.AiUsageResponse;
import com.study.moya.token.dto.usage.UseTokenRequest;
import com.study.moya.token.service.TokenFacadeService;
import com.study.moya.token.service.ticket.TicketFacadeService;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.object.BatchSqlUpdate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class RoadmapService {

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

    private final TicketFacadeService ticketFacadeService;

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private static final Long ROADMAP_SERVICE_ID = 1L;
    // etc 타입 상수 정의
    public static final Long MAIN_CATEGORY = 1L;    // 대분류(etc1)
    public static final Long SUB_CATEGORY = 2L;     // 중분류(etc2)

    @Async("taskExecutor")
    public CompletableFuture<WeeklyRoadmapResponse> generateWeeklyRoadmapAsync(RoadmapRequest request, Long memberId) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("로드맵 생성 시작");
            Long usageId = null;

            String systemPrompt = promptService.createSystemPrompt(request);
            log.info("===================시스템 프롬프트=================== \n" + systemPrompt);

            String prompt = promptService.createPrompt(request);
            log.info("생성된 Prompt:\n{}", prompt);

            // 1. 로드맵 티켓 확인 및 사용 (없으면 바로 예외) 잠깐 추가
            try {
                if (!ticketFacadeService.hasTicket(memberId, TicketType.ROADMAP_TICKET)) {
                    throw new RuntimeException("로드맵 티켓이 부족합니다");
                }

                TicketUsageResponse ticketResponse = ticketFacadeService.useTicket(memberId, TicketType.ROADMAP_TICKET);
                usageId = ticketResponse.getId();
                log.info("로드맵 티켓 사용 완료. 사용 ID: {}", usageId);

            } catch (Exception e) {
                log.error("티켓 확인/사용 실패: {}", e.getMessage());
                throw new RuntimeException("티켓이 부족합니다", e);
            }

            //여기까지=============================================


            UseTokenRequest tokenRequest = UseTokenRequest.builder()
                    .aiServiceId(ROADMAP_SERVICE_ID)
                    .requestData(prompt)
                    .build();

//            try {
//                AiUsageResponse usageResponse = tokenFacadeService.useTokenForAiService(memberId, tokenRequest);
//                usageId = usageResponse.getId();
//                log.info("토큰 차감 완료. 사용 ID: {}", usageId);
//            } catch (Exception e) {
//                log.error("토큰 차감 실패: {}", e.getMessage());
//                throw new RuntimeException("토큰 차감 실패", e);
//            }

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(roadmapModel)
                    .messages(promptService.buildMessages(systemPrompt, prompt))
                    .temperature(0.8)
                    .maxTokens(2000)
                    .build();

            log.info("OpenAI API 요청 준비 완료: {}", completionRequest);

            try {
                log.info("OpenAI API 호출 시작...");
                ChatCompletionResult chatCompletion = openAiService.createChatCompletion(completionRequest);

                String apiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();
                log.info("OpenAI API 응답 수신 완료:\n{}", apiResponse);

                // 6. 토큰 사용량 로깅
                Usage usage = chatCompletion.getUsage();
                logTokenUsage(usage);

                // 7. 실제 사용량 기록 (선택 사항)
                if (usage != null && usageId != null) {
                    try {
                        tokenFacadeService.updateActualTokenUsage(usageId, usage.getTotalTokens());
                    } catch (Exception e) {
                        log.error("실제 사용량 업데이트 실패: {}", e.getMessage());
                        // 실제 사용량 업데이트 실패는 로드맵 생성 자체에 영향을 주지 않음
                    }
                }

                // 응답 파싱
                WeeklyRoadmapResponse response = responseParser.parseResponse(apiResponse);

                Category category = null;
                if (request.getMainCategory() != null && !request.getMainCategory().trim().isEmpty()) {
                    // 카테고리가 있는 경우만 찾기
                    category = categoryRepository.findByName(request.getMainCategory())
                            .orElseThrow(() -> new IllegalArgumentException("카테고리를 찾을 수 없습니다: " + request.getMainCategory()));
                }

                // 파싱된 응답을 엔티티로 저장
                saveCurriculum(Integer.parseInt(request.getCurrentLevel()) + 1, request.getSubCategory(),
                        request.getDuration(), request.getLearningObjective(), response,
                        category, memberId, request.getEtc1(), request.getEtc2());

                //잠깐 추가!
                try {
                    ticketFacadeService.markTicketUsageAsCompleted(usageId);
                    log.info("티켓 사용 완료 처리: {}", usageId);
                } catch (Exception e) {
                    log.error("티켓 사용 완료 처리 실패: {}", e.getMessage());
                    // 상태 업데이트 실패는 로드맵 생성 자체에 영향을 주지 않음
                }
                //잠깐 추가!

                // 9. AI 사용 내역 완료 처리
                try {
                    tokenFacadeService.markAiUsageAsCompleted(usageId);
                    log.info("AI 사용 내역 완료 처리: {}", usageId);
                } catch (Exception e) {
                    log.error("AI 사용 내역 완료 처리 실패: {}", e.getMessage());
                    // 상태 업데이트 실패는 로드맵 생성 자체에 영향을 주지 않음
                }


                return response;
            } catch (Exception e) {
                log.error("OpenAI API 호출 중 예외 발생:", e);
                // 10. API 호출 실패 시 토큰 환불 처리
                if (usageId != null) {
                    try {
                        tokenFacadeService.markAiUsageAsFailed(usageId);
                        log.info("토큰 환불 처리 완료: {}", usageId);
                        //잠깐 추가
                        ticketFacadeService.markTicketUsageAsFailed(usageId);
                        log.info("티켓 환불 처리 완료: {}", usageId);
                        //잠깐 추가
                    } catch (Exception ex) {
                        log.error("토큰 환불 처리 실패: {}", ex.getMessage());
                        //잠깐 추가2
                        log.error("티켓 환불 처리 실패: {}", ex.getMessage());
                        //잠깐 추가2
                    }
                }

                throw new RuntimeException("OpenAI API 호출 실패", e);
            }
        });
    }

    public Long saveCurriculum(int goalLevel, String topic, int duration, String learningObjective,
                               WeeklyRoadmapResponse response, Category category,
                               Long memberId, String etc1Name, String etc2Name) {
        log.info("커리큘럼 저장 시작");

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

        // 먼저 RoadMap을 저장하여 ID를 확보
        RoadMap savedRoadMap = roadMapRepository.save(roadMap);

        // WeeklyPlan 생성 및 저장
        for (WeeklyRoadmapResponse.WeeklyPlan weeklyPlanDto : response.getWeeklyPlans()) {
            WeeklyPlan weeklyPlan = WeeklyPlan.builder()
                    .weekNumber(weeklyPlanDto.getWeek())
                    .keyword(weeklyPlanDto.getWeeklyKeyword())
                    .roadMap(savedRoadMap)  // 저장된 RoadMap 참조
                    .build();

            // WeeklyPlan 저장
            WeeklyPlan savedWeeklyPlan = weeklyPlanRepository.save(weeklyPlan);

            // DailyPlan 생성 및 저장
            for (WeeklyRoadmapResponse.DailyPlan dailyPlanDto : weeklyPlanDto.getDailyPlans()) {
                DailyPlan dailyPlan = DailyPlan.builder()
                        .dayNumber(dailyPlanDto.getDay())
                        .keyword(dailyPlanDto.getDailyKeyword())
                        .weeklyPlan(savedWeeklyPlan)  // 저장된 WeeklyPlan 참조
                        .build();

                dailyPlanRepository.save(dailyPlan);
            }
        }

        if (memberId != null){
            subscribeToRoadMap(memberId, savedRoadMap.getId());
        }

        log.info("커리큘럼 저장 완료. ID: {}", savedRoadMap.getId());

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

    //    @Transactional
//    public Long saveCurriculum(int goalLevel, String topic, int duration, WeeklyRoadmapResponse response) {
//        log.info("커리큘럼 벌크 저장 시작");
//
//        // 1. Roadmap 벌크 INSERT
//        String roadmapBulkInsert = """
//            INSERT INTO roadmaps (duration, goal_level, topic, evaluation, created_at, modified_at)
//            VALUES (:duration, :goalLevel, :topic, :evaluation, :now, :now)
//            """;
//
//        MapSqlParameterSource roadmapParams = new MapSqlParameterSource()
//                .addValue("duration", duration)
//                .addValue("goalLevel", goalLevel)
//                .addValue("topic", topic)
//                .addValue("evaluation", response.getCurriculumEvaluation())
//                .addValue("now", LocalDateTime.now());
//
//        KeyHolder keyHolder = new GeneratedKeyHolder();
//        namedParameterJdbcTemplate.update(roadmapBulkInsert, roadmapParams, keyHolder);
//        Long roadmapId = keyHolder.getKey().longValue();
//
//        // 2. Tips 벌크 INSERT - 위치 기반 파라미터 사용
//        String tipsBulkInsert = """
//            INSERT INTO roadmap_tips (roadmap_id, tip)
//            VALUES (?, ?)
//            """;
//
//        jdbcTemplate.batchUpdate(tipsBulkInsert,
//                response.getOverallTips().stream()
//                        .map(tip -> new Object[]{roadmapId, tip})
//                        .collect(Collectors.toList()));
//
//        // 3. WeeklyPlans 벌크 INSERT
//        String weeklyPlansBulkInsert = """
//            INSERT INTO weekly_plans (roadmap_id, week_number, keyword, created_at, modified_at)
//            VALUES (:roadmapId, :weekNumber, :keyword, :now, :now)
//            """;
//
//        List<Map<String, Object>> weeklyBatchParams = response.getWeeklyPlans().stream()
//                .map(wp -> {
//                    Map<String, Object> params = new HashMap<>();
//                    params.put("roadmapId", roadmapId);
//                    params.put("weekNumber", wp.getWeek());
//                    params.put("keyword", wp.getWeeklyKeyword());
//                    params.put("now", LocalDateTime.now());
//                    return params;
//                })
//                .collect(Collectors.toList());
//
//        namedParameterJdbcTemplate.batchUpdate(weeklyPlansBulkInsert,
//                weeklyBatchParams.toArray(new Map[0]));
//
//        // 4. DailyPlans 벌크 INSERT
//        String dailyPlansBulkInsert = """
//            INSERT INTO daily_plans (weekly_plan_id, day_number, keyword, created_at, modified_at)
//            SELECT wp.id, :dayNumber, :keyword, :now, :now
//            FROM weekly_plans wp
//            WHERE wp.roadmap_id = :roadmapId AND wp.week_number = :weekNumber
//            """;
//
//        List<Map<String, Object>> dailyBatchParams = response.getWeeklyPlans().stream()
//                .flatMap(wp -> wp.getDailyPlans().stream()
//                        .map(dp -> {
//                            Map<String, Object> params = new HashMap<>();
//                            params.put("roadmapId", roadmapId);
//                            params.put("weekNumber", wp.getWeek());
//                            params.put("dayNumber", dp.getDay());
//                            params.put("keyword", dp.getDailyKeyword());
//                            params.put("now", LocalDateTime.now());
//                            return params;
//                        }))
//                .collect(Collectors.toList());
//
//        namedParameterJdbcTemplate.batchUpdate(dailyPlansBulkInsert,
//                dailyBatchParams.toArray(new Map[0]));
//
//        log.info("커리큘럼 벌크 저장 완료. ID: {}", roadmapId);
//        return roadmapId;
//    }
//
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

    public List<RoadMapSimpleDto> getRoadMapsByCategory(Long categoryId) {
        return roadMapRepository.findRoadMapsByCategoryId(categoryId);
    }
}