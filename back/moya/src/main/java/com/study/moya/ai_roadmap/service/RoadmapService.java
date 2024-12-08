package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import com.study.moya.ai_roadmap.domain.RoadMap;
import com.study.moya.ai_roadmap.domain.WeeklyPlan;
import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import com.study.moya.ai_roadmap.repository.RoadMapRepository;
import com.study.moya.ai_roadmap.util.RoadmapResponseParser;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import java.util.concurrent.CompletableFuture;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class RoadmapService {

    @Value("${openai.api.models.roadmap_generation.model}")
    private String roadmapModel;

    private final OpenAiService openAiService;
    private final RoadmapPromptService promptService;
    private final RoadmapResponseParser responseParser;
    private final RoadMapRepository roadMapRepository;

    public RoadmapService(
            OpenAiService openAiService,
            RoadmapPromptService promptService,
            RoadmapResponseParser responseParser,
            RoadMapRepository roadMapRepository
    ) {
        this.openAiService = openAiService;
        this.promptService = promptService;
        this.responseParser = responseParser;
        this.roadMapRepository = roadMapRepository;
    }

    @Async
    public CompletableFuture<WeeklyRoadmapResponse> generateWeeklyRoadmapAsync(RoadmapRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("로드맵 생성 시작");

            String prompt = promptService.createPrompt(request);
            log.info("생성된 Prompt:\n{}", prompt);

            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(roadmapModel)
                    .messages(promptService.buildMessages(prompt))
                    .temperature(0.8)
                    .maxTokens(2000)
                    .build();

            log.info("OpenAI API 요청 준비 완료: {}", completionRequest);

            try {
                log.info("OpenAI API 호출 시작...");
                ChatCompletionResult chatCompletion = openAiService.createChatCompletion(completionRequest);

                String apiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();
                log.info("OpenAI API 응답 수신 완료:\n{}", apiResponse);

                logTokenUsage(chatCompletion.getUsage());

                // 응답 파싱
                WeeklyRoadmapResponse response = responseParser.parseResponse(apiResponse);

                // 파싱된 응답을 엔티티로 저장
                saveCurriculum(request.getSubCategory(), response);

                return response;
            } catch (Exception e) {
                log.error("OpenAI API 호출 중 예외 발생:", e);
                throw new RuntimeException("OpenAI API 호출 실패", e);
            }
        });
    }

    @Transactional
    public Long saveCurriculum(String topic, WeeklyRoadmapResponse response) {
        log.info("커리큘럼 저장 시작");

        RoadMap curriculum = RoadMap.builder()
                .topic(topic)
                .evaluation(response.getCurriculumEvaluation())
                .overallTips(response.getOverallTips())
                .build();

        for (WeeklyRoadmapResponse.WeeklyPlan weeklyPlanDto : response.getWeeklyPlans()) {
            WeeklyPlan weeklyPlan = WeeklyPlan.builder()
                    .weekNumber(weeklyPlanDto.getWeek())
                    .keyword(weeklyPlanDto.getWeeklyKeyword())
                    .build();

            for (WeeklyRoadmapResponse.DailyPlan dailyPlanDto : weeklyPlanDto.getDailyPlans()) {
                DailyPlan dailyPlan = DailyPlan.builder()
                        .dayNumber(dailyPlanDto.getDay())
                        .keyword(dailyPlanDto.getDailyKeyword())
                        .build();

                weeklyPlan.addDailyPlan(dailyPlan);
            }

            curriculum.addWeeklyPlan(weeklyPlan);
        }

        RoadMap savedCurriculum = roadMapRepository.save(curriculum);
        log.info("커리큘럼 저장 완료. ID: {}", savedCurriculum.getId());

        return savedCurriculum.getId();
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