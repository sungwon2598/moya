package com.study.moya.admin.service;

import com.study.moya.admin.dto.roadmap.request.WorksheetUpdateRequest;
import com.study.moya.admin.dto.roadmap.response.WorksheetResponse;
import com.study.moya.ai_roadmap.domain.DailyPlan;
import com.study.moya.ai_roadmap.dto.request.WorkSheetRequest;
import com.study.moya.ai_roadmap.repository.DailyPlanRepository;
import com.study.moya.ai_roadmap.service.WorksheetPromptService;
import com.study.moya.ai_roadmap.util.WorksheetResponseParser;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AdminWorksheetService {

    @Value("${openai.api.models.worksheet_generation.model}")
    private String worksheetModel;

    private static final int MAX_RETRIES = 3;  // 최대 재시도 횟수
    private static final long DELAY_MS = 1000;  // 재시도 간 대기 시간 (1초)

    private final OpenAiService openAiService;
    private final WorksheetPromptService promptService;
    private final WorksheetResponseParser worksheetResponseParser;
    private final DailyPlanRepository dailyPlanRepository;
    @Qualifier("taskExecutor")
    private final ThreadPoolTaskExecutor taskExecutor;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> generateAllWorksheets(Long roadmapId, WorkSheetRequest request) {
        log.info("어드민 - 로드맵 ID: {}의 전체 학습지 생성 시작", roadmapId);

        try {
            List<DailyPlan> allDailyPlans = dailyPlanRepository.findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(roadmapId);
            List<List<DailyPlan>> groupedPlans = splitIntoGroups(allDailyPlans, 3);

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (List<DailyPlan> planGroup : groupedPlans) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    log.info("어드민 - 그룹 처리 시작: {}일차 ~ {}일차",
                            planGroup.getFirst().getDayNumber(),
                            planGroup.getLast().getDayNumber());

                    try {
                        generateWorksheetForGroup(planGroup, request);  // 어드민용 메서드 호출
                    } catch (Exception e) {
                        log.error("어드민 - 그룹 학습지 생성 중 오류: {}일차 그룹, 오류: {}",
                                planGroup.getFirst().getDayNumber(), e.getMessage(), e);
                    }
                }, taskExecutor);

                futures.add(future);
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        } catch (Exception e) {
            log.error("어드민 - 학습지 생성 준비 중 오류: {}", e.getMessage(), e);
            CompletableFuture<Void> failedFuture = new CompletableFuture<>();
            failedFuture.completeExceptionally(e);
            return failedFuture;
        }
    }

    private List<List<DailyPlan>> splitIntoGroups(List<DailyPlan> allPlans, int groupSize) {
        List<List<DailyPlan>> groups = new ArrayList<>();
        for (int i = 0; i < allPlans.size(); i += groupSize) {
            int end = Math.min(i + groupSize, allPlans.size());
            groups.add(allPlans.subList(i, end));
        }
        return groups;
    }

    private void generateWorksheetForGroup(List<DailyPlan> planGroup, WorkSheetRequest request) {
        String initialPrompt = promptService.createPrompt(planGroup, request);
        Map<Integer, String> dayWorksheets = retryChatCompletion(initialPrompt, planGroup);

        updateDailyPlansWithWorksheet(planGroup, dayWorksheets);
        log.info("학습지 생성 완료 - {} ~ {}일차", planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber());
    }

    private Map<Integer, String> retryChatCompletion(String prompt, List<DailyPlan> planGroup) {
        AtomicInteger retryCount = new AtomicInteger(0);

        while (retryCount.get() < MAX_RETRIES) {
            try {
                ChatCompletionResult chatCompletion = createChatCompletion(prompt, planGroup, retryCount.get() + 1);
                String worksheetContent = chatCompletion.getChoices().getFirst().getMessage().getContent();
                Map<Integer, String> dayWorksheets = worksheetResponseParser.parseResponse(worksheetContent);

                if (!dayWorksheets.isEmpty()) {
                    log.info("어드민 - 토큰 사용량 {}일 ~ {}일", planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber());
                    logTokenUsage(chatCompletion.getUsage());  // 정보용 로깅만 유지
                    return dayWorksheets;
                }
                throw new IllegalStateException("파싱된 학습 가이드가 없습니다.");

            } catch (Exception e) {
                int attempts = retryCount.incrementAndGet();
                if (attempts >= MAX_RETRIES) {
                    log.error("어드민 - 최대 재시도 횟수({}) 초과, 학습지 생성 실패 - {} ~ {}일차: {}",
                            MAX_RETRIES, planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber(), e.getMessage());
                    throw new RuntimeException("학습지 생성 실패: 최대 재시도 초과", e);
                }
                log.warn("어드민 - 학습지 생성 실패, 재시도 {}회 시도 - {} ~ {}일차: {}",
                        attempts, planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber(), e.getMessage());
                try {
                    Thread.sleep(DELAY_MS * attempts);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    throw new RuntimeException("재시도 대기 중 인터럽트 발생", ie);
                }
            }
        }
        throw new RuntimeException("학습지 생성 실패: 알 수 없는 오류");
    }

    private ChatCompletionResult createChatCompletion(String prompt, List<DailyPlan> planGroup, int attempt) {

        log.info("학습지 생성 API 호출 시작 - {} ~ {}일차, 시도 횟수: {}",
                planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber(), attempt);


        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(worksheetModel)
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .temperature(0.7)
                .maxTokens(2500)
                .build();

        return openAiService.createChatCompletion(completionRequest);
    }

    protected void updateDailyPlansWithWorksheet(List<DailyPlan> plans, Map<Integer, String> dayWorksheets) {
        for (DailyPlan plan : plans) {
            String worksheet = dayWorksheets.get(plan.getDayNumber());
            if (worksheet != null) {
                plan.updateWorkSheet(worksheet);
                dailyPlanRepository.save(plan);
                log.debug("{}일차 학습지 저장 완료", plan.getDayNumber());
            } else {
                log.warn("{}일차 학습지 내용이 없습니다", plan.getDayNumber());
            }
        }
    }

    private void logTokenUsage(Usage usage) {
        if (usage != null) {
            log.info("토큰 사용량 - Prompt: {}, Completion: {}, Total: {}",
                    usage.getPromptTokens(),
                    usage.getCompletionTokens(),
                    usage.getTotalTokens());
        } else {
            log.warn("토큰 사용량 정보를 가져올 수 없습니다.");
        }
    }

    @Transactional(readOnly = true)
    public long getWorksheetProgress(Long roadmapId) {
        long totalPlans = dailyPlanRepository.countTotalDailyPlans(roadmapId);
        long pendingPlans = dailyPlanRepository.countPendingWorksheets(roadmapId);
        return ((totalPlans - pendingPlans) * 100) / totalPlans;
    }

    @Transactional(readOnly = true)
    public List<WorksheetResponse> getAllWorksheetsByRoadmapId(Long roadmapId) {
        return dailyPlanRepository.findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(roadmapId)
                .stream()
                .filter(plan -> plan.getWorkSheet() != null && !plan.getWorkSheet().isEmpty())
                .map(plan -> WorksheetResponse.builder()
                        .dailyPlanId(plan.getId())
                        .weekNumber(plan.getWeeklyPlan().getWeekNumber())
                        .dayNumber(plan.getDayNumber())
                        .keyword(plan.getKeyword())
                        .worksheet(plan.getWorkSheet())
                        .build())
                .toList();
    }

    @Transactional
    public void updateWorksheet(Long dailyPlanId, WorksheetUpdateRequest request) {
        log.info("일간계획 ID: {}의 워크시트 수정 시작", dailyPlanId);

        DailyPlan dailyPlan = dailyPlanRepository.findById(dailyPlanId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일간계획입니다. ID: " + dailyPlanId));

        String oldWorksheet = dailyPlan.getWorkSheet();
        dailyPlan.updateWorkSheet(request.getWorksheet());

        dailyPlanRepository.save(dailyPlan);

        log.info("일간계획 ID: {}의 워크시트 수정 완료. 기존 길이: {}, 새로운 길이: {}",
                dailyPlanId,
                oldWorksheet != null ? oldWorksheet.length() : 0,
                request.getWorksheet().length());
    }

    /**
     * 워크시트 삭제 (내용을 null로 변경)
     */
    @Transactional
    public void deleteWorksheet(Long dailyPlanId) {
        log.info("일간계획 ID: {}의 워크시트 삭제 시작", dailyPlanId);

        DailyPlan dailyPlan = dailyPlanRepository.findById(dailyPlanId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 일간계획입니다. ID: " + dailyPlanId));

        String currentWorksheet = dailyPlan.getWorkSheet();
        dailyPlan.updateWorkSheet(null);

        dailyPlanRepository.save(dailyPlan);

        log.info("일간계획 ID: {}의 워크시트 삭제 완료. 삭제된 워크시트 길이: {}",
                dailyPlanId,
                currentWorksheet != null ? currentWorksheet.length() : 0);
    }
}
