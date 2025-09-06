package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import com.study.moya.ai_roadmap.dto.request.WorkSheetRequest;
import com.study.moya.ai_roadmap.repository.DailyPlanRepository;
import com.study.moya.ai_roadmap.util.WorksheetResponseParser;
import com.study.moya.token.domain.enums.TicketType;
import com.study.moya.token.dto.ticket.reponse.TicketUsageResponse;
import com.study.moya.token.service.ticket.TicketFacadeService;
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

    private final TicketFacadeService ticketFacadeService;

    @Qualifier("taskExecutor")
    private final ThreadPoolTaskExecutor taskExecutor;

    @Async("taskExecutor")
    @Transactional
    public CompletableFuture<Void> generateAllWorksheets(Long roadmapId, WorkSheetRequest request, Long memberId) {
        log.info("로드맵 ID: {}의 전체 학습지 생성 시작", roadmapId);
        log.info("현재 스레드: {}, 활성 스레드 수: {}, 풀 크기: {}",
                Thread.currentThread().getName(), taskExecutor.getActiveCount(), taskExecutor.getPoolSize());


        Long usageId = null;

        // 1. 로드맵 티켓 확인 및 사용 (없으면 바로 예외) 잠깐 추가
        try {
            if (!ticketFacadeService.hasTicket(memberId, TicketType.ROADMAP_TICKET)) {
                throw new RuntimeException("워크시트 티켓이 부족합니다");
            }

            TicketUsageResponse ticketResponse = ticketFacadeService.useTicket(memberId, TicketType.WORKSHEET_TICKET);
            usageId = ticketResponse.getId();
            log.info("워크시트 티켓 사용 완료. 사용 ID: {}", usageId);

        } catch (Exception e) {
            log.error("티켓 확인/사용 실패: {}", e.getMessage());
            throw new RuntimeException("티켓이 부족합니다", e);
        }

        try {
            List<DailyPlan> allDailyPlans = dailyPlanRepository.findAllByWeeklyPlan_RoadMap_IdOrderByDayNumber(roadmapId);
            List<List<DailyPlan>> groupedPlans = splitIntoGroups(allDailyPlans, 3);

            List<CompletableFuture<Void>> futures = new ArrayList<>();

            for (List<DailyPlan> planGroup : groupedPlans) {
                CompletableFuture<Void> future = CompletableFuture.runAsync(() -> {
                    log.info("그룹 처리 시작 - 스레드: {}, 일차: {} ~ {}",
                            Thread.currentThread().getName(),
                            planGroup.getFirst().getDayNumber(),
                            planGroup.getLast().getDayNumber());

                    try {
                        generateWorksheetForGroup(planGroup, request);
                    } catch (Exception e) {
                        log.error("그룹 학습지 생성 중 오류: {}일차 그룹, 오류: {}",
                                planGroup.getFirst().getDayNumber(), e.getMessage(), e);
                    }

                    log.info("그룹 처리 완료 - 스레드: {}", Thread.currentThread().getName());
                }, taskExecutor); // 중요: 동일한 taskExecutor 사용

                futures.add(future);
            }

            return CompletableFuture.allOf(futures.toArray(new CompletableFuture[0]));
        } catch (Exception e) {
            log.error("학습지 생성 준비 중 오류: {}", e.getMessage(), e);
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