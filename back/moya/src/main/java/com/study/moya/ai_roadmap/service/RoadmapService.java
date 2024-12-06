package com.study.moya.ai_roadmap.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.ai_roadmap.dto.RoadmapRequest;
import com.study.moya.ai_roadmap.dto.WeeklyRoadmapResponse;
import com.theokanning.openai.Usage;
import com.theokanning.openai.completion.chat.ChatCompletionResult;
import com.theokanning.openai.service.OpenAiService;
import com.theokanning.openai.completion.chat.ChatCompletionRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
public class RoadmapService {

    @Value("${openai.api.models.roadmap_generation.model}")
    private String roadmapModel;
    @Value("${openai.api.models.category_classification.model}")
    private String searchModel;
    @Value("${chatgpt.system-prompt}")
    private String systemPrompt;
    @Value("${chatgpt.validation-prompt}")
    private String validationPrompt;

    private final OpenAiService openAiService;

    public RoadmapService(@Value("${openai.api.key}") String apiKey) {
        // 300초(5분) 타임아웃 설정
        // 로드맵 특성상 응답시간이 길기 떄문에 이게 짧으면 답장이 오지 않음
        Duration timeout = Duration.ofSeconds(300);

        this.openAiService = new OpenAiService(apiKey, timeout);
    }

    @Async
    public CompletableFuture<WeeklyRoadmapResponse> generateWeeklyRoadmapAsync(RoadmapRequest request) {
        return CompletableFuture.supplyAsync(() -> {
            log.info("시작");

            // Prompt 생성
            String prompt = createPrompt(request);
            log.info("생성된 Prompt:\n{}", prompt);

            // ChatCompletionRequest 생성
            ChatCompletionRequest completionRequest = ChatCompletionRequest.builder()
                    .model(roadmapModel)
                    .messages(List.of(
                            new ChatMessage("system", systemPrompt),
                            new ChatMessage("user", prompt)
                    ))
                    .temperature(0.7)
                    .maxTokens(2000)
                    .build();

            log.info("OpenAI API 요청 준비 완료: {}", completionRequest);

            try {
                // OpenAI API 호출
                log.info("OpenAI API 호출 시작...");
                ChatCompletionResult chatCompletion = openAiService.createChatCompletion(completionRequest);

                // OpenAI 응답 처리
                String apiResponse = chatCompletion.getChoices().get(0).getMessage().getContent();
                log.info("OpenAI API 응답 수신 완료:\n{}", apiResponse);

                // 토큰 사용량 로그 출력
                Usage usage = chatCompletion.getUsage();
                if (usage != null) {
                    log.info("OpenAI 토큰 사용량 - Prompt Tokens: {}, Completion Tokens: {}, Total Tokens: {}",
                            usage.getPromptTokens(),
                            usage.getCompletionTokens(),
                            usage.getTotalTokens());
                } else {
                    log.warn("OpenAI 토큰 사용량 정보를 가져올 수 없습니다.");
                }

                return parseResponse(apiResponse);
            } catch (Exception e) {
                // 예외 처리 및 상세 로그 출력
                log.error("OpenAI API 호출 중 예외 발생:", e);
                throw new RuntimeException("OpenAI API 호출 실패", e);
            }
        });
    }


    private List<ChatMessage> buildMessages(String systemPrompt, String userPrompt) {
        return List.of(
                new ChatMessage("system", String.format(systemPrompt)),
                new ChatMessage("user", userPrompt)
        );
    }

    private String createPrompt(RoadmapRequest request) {
        return String.format(
                "대분류: %s\n" +
                        "중분류: %s\n" +
                        "현재 수준: %s\n" +
                        "목표 수준: %s\n" +
                        "기간: %s주\n\n" +
                        "위의 정보를 기반으로 주차별 키워드와 일별 키워드를 포함한 로드맵을 작성해주세요.",
                request.getMainCategory(),
                request.getSubCategory(),
                request.getCurrentLevel(),
                request.getGoalLevel(),
                request.getDuration()
        );
    }

    private WeeklyRoadmapResponse parseResponse(String apiResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            // weeklyPlans 파싱
            List<WeeklyRoadmapResponse.WeeklyPlan> weeklyPlans = new ArrayList<>();
            JsonNode weeklyPlansNode = rootNode.get("weeklyPlans");

            for (JsonNode weeklyPlanNode : weeklyPlansNode) {
                int week = weeklyPlanNode.get("week").asInt();
                String weeklyKeyword = weeklyPlanNode.get("weeklyKeyword").asText();

                // dailyPlans 파싱
                List<WeeklyRoadmapResponse.DailyPlan> dailyPlans = new ArrayList<>();
                JsonNode dailyPlansNode = weeklyPlanNode.get("dailyPlans");
                for (JsonNode dailyPlanNode : dailyPlansNode) {
                    int day = dailyPlanNode.get("day").asInt();
                    String dailyKeyword = dailyPlanNode.get("dailyKeyword").asText();
                    dailyPlans.add(new WeeklyRoadmapResponse.DailyPlan(day, dailyKeyword));
                }

                weeklyPlans.add(new WeeklyRoadmapResponse.WeeklyPlan(week, weeklyKeyword, dailyPlans));
            }

            // overallTips, curriculumEvaluation, hasRestrictedTopics 파싱
            List<String> overallTips = objectMapper.convertValue(
                    rootNode.get("overallTips"),
                    new TypeReference<>() {}
            );
            String curriculumEvaluation = rootNode.get("curriculumEvaluation").asText();
            String hasRestrictedTopics = rootNode.get("hasRestrictedTopics").asText();

            return new WeeklyRoadmapResponse(weeklyPlans, overallTips, curriculumEvaluation, hasRestrictedTopics);
        } catch (Exception e) {
            log.error("Error parsing API response: {}", apiResponse, e);
            throw new RuntimeException("Failed to parse API response", e);
        }
    }

}
