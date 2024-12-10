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
                    Thread.sleep(1000);
                } catch (Exception e) {
                    log.error("그룹 학습지 생성 중 오류 발생: {}일차 그룹, 오류: {}",
                            planGroup.get(0).getDayNumber(), e.getMessage());
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
        String prompt = promptService.createPrompt(planGroup);

        ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                .model(worksheetModel)
                .messages(List.of(new ChatMessage(ChatMessageRole.USER.value(), prompt)))
                .temperature(0.7)
                .maxTokens(2500)
                .build();

        try {
            log.info("학습지 생성 API 호출 시작 - {} ~ {}일차",
                    planGroup.get(0).getDayNumber(),
                    planGroup.get(planGroup.size() - 1).getDayNumber());

            ChatCompletionResult chatCompletion = openAiService.createChatCompletion(completionRequest);
            String worksheetContent = chatCompletion.getChoices().get(0).getMessage().getContent();

            updateDailyPlansWithWorksheet(planGroup, worksheetContent);
            logTokenUsage(chatCompletion.getUsage());

            log.info("학습지 생성 완료 - {} ~ {}일차",
                    planGroup.get(0).getDayNumber(),
                    planGroup.get(planGroup.size() - 1).getDayNumber());

        } catch (Exception e) {
            log.error("학습지 생성 중 오류 발생:", e);
            throw new RuntimeException("학습지 생성 실패", e);
        }
    }

    @Transactional
    protected void updateDailyPlansWithWorksheet(List<DailyPlan> plans, String worksheetContent) {
        Map<Integer, String> dayWorksheets = worksheetResponseParser.parseResponse(worksheetContent);

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