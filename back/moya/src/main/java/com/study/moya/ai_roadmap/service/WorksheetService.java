package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import com.study.moya.ai_roadmap.repository.DailyPlanRepository;
import com.study.moya.ai_roadmap.util.WorksheetResponseParser;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.completion.chat.ChatMessage;
import com.theokanning.openai.completion.chat.ChatMessageRole;
import com.theokanning.openai.service.OpenAiService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.atomic.AtomicInteger;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorksheetService {

    @Value("${openai.api.models.worksheet_generation.model}")
    private String worksheetModel;

    private static final int MAX_RETRIES = 3;  // 최대 재시도 횟수
    private static final long DELAY_MS = 1000;  // 재시도 간 대기 시간 (1초)

    private final OpenAiService openAiService;
    private final WorksheetPromptService promptService;
    private final WorksheetResponseParser worksheetResponseParser;
    private final DailyPlanRepository dailyPlanRepository;

    @Async
    @Transactional
    public CompletableFuture<Void> generateAllWorksheets(Long roadmapId) {
        return CompletableFuture.runAsync(() -> {
            log.info("로드맵 ID: {}의 전체 학습지 생성 시작", roadmapId);

            List<DailyPlan> allDailyPlans = dailyPlanRepository.findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(
                    roadmapId);
            List<List<DailyPlan>> groupedPlans = splitIntoGroups(allDailyPlans, 3);

            for (List<DailyPlan> planGroup : groupedPlans) {
                try {
                    generateWorksheetForGroup(planGroup);
                    // API 요청 제한을 고려한 대기 시간
                    Thread.sleep(DELAY_MS);
                } catch (Exception e) {
                    log.error("그룹 학습지 생성 중 오류 발생: {}일차 그룹, 오류: {}",
                            planGroup.getFirst().getDayNumber(), e.getMessage());
                }
            }

            log.info("로드맵 ID: {}의 전체 학습지 생성 완료", roadmapId);
        });
    }

    private List<List<DailyPlan>> splitIntoGroups(List<DailyPlan> allPlans, int groupSize) {
        List<List<DailyPlan>> groups = new ArrayList<>();
        for (int i = 0; i < allPlans.size(); i += groupSize) {
            int end = Math.min(i + groupSize, allPlans.size());
            groups.add(allPlans.subList(i, end));
        }
        return groups;
    }

    private void generateWorksheetForGroup(List<DailyPlan> planGroup) {
        String initialPrompt = promptService.createPrompt(planGroup);
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
                    log.info("=======================토큰 생성 {}일 ~ {}일 ====================================", planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber());
                    logTokenUsage(chatCompletion.getUsage());
                    return dayWorksheets;
                }
                throw new IllegalStateException("파싱된 학습 가이드가 없습니다.");

            } catch (Exception e) {
                int attempts = retryCount.incrementAndGet();
                if (attempts >= MAX_RETRIES) {
                    log.error("최대 재시도 횟수({}) 초과, 학습지 생성 실패 - {} ~ {}일차: {}",
                            MAX_RETRIES, planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber(), e.getMessage());
                    throw new RuntimeException("학습지 생성 실패: 최대 재시도 초과", e);
                }
                log.warn("학습지 생성 실패, 재시도 {}회 시도 - {} ~ {}일차: {}",
                        attempts, planGroup.getFirst().getDayNumber(), planGroup.getLast().getDayNumber(), e.getMessage());
                try {
                    Thread.sleep(DELAY_MS * attempts); // 지수 백오프
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
}