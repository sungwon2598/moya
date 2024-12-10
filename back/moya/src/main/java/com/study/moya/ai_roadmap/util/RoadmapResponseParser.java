package com.study.moya.ai_roadmap.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.study.moya.ai_roadmap.dto.response.WeeklyRoadmapResponse;
import java.util.ArrayList;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RoadmapResponseParser {

    private final ObjectMapper objectMapper;

    public RoadmapResponseParser(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public WeeklyRoadmapResponse parseResponse(String apiResponse) {
        try {
            JsonNode rootNode = objectMapper.readTree(apiResponse);

            List<WeeklyRoadmapResponse.WeeklyPlan> weeklyPlans = parseWeeklyPlans(rootNode.get("weeklyPlans"));
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

    private List<WeeklyRoadmapResponse.WeeklyPlan> parseWeeklyPlans(JsonNode weeklyPlansNode) {
        List<WeeklyRoadmapResponse.WeeklyPlan> weeklyPlans = new ArrayList<>();

        for (JsonNode weeklyPlanNode : weeklyPlansNode) {
            int week = weeklyPlanNode.get("week").asInt();
            String weeklyKeyword = weeklyPlanNode.get("weeklyKeyword").asText();
            List<WeeklyRoadmapResponse.DailyPlan> dailyPlans = parseDailyPlans(weeklyPlanNode.get("dailyPlans"));

            weeklyPlans.add(new WeeklyRoadmapResponse.WeeklyPlan(week, weeklyKeyword, dailyPlans));
        }

        return weeklyPlans;
    }

    private List<WeeklyRoadmapResponse.DailyPlan> parseDailyPlans(JsonNode dailyPlansNode) {
        List<WeeklyRoadmapResponse.DailyPlan> dailyPlans = new ArrayList<>();

        for (JsonNode dailyPlanNode : dailyPlansNode) {
            int day = dailyPlanNode.get("day").asInt();
            String dailyKeyword = dailyPlanNode.get("dailyKeyword").asText();
            dailyPlans.add(new WeeklyRoadmapResponse.DailyPlan(day, dailyKeyword));
        }

        return dailyPlans;
    }
}