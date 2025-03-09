package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.dto.request.RoadmapRequest;
import com.theokanning.openai.completion.chat.ChatMessage;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class RoadmapPromptService {

    @Value("${chatgpt.system-prompt}")
    private String systemPrompt;

    public String createSystemPrompt(RoadmapRequest request){
        return String.format(
                systemPrompt,
                request.getSubCategory(),
                request.getMainCategory(),
                request.getSubCategory(),
                request.getCurrentLevel(),
                request.getDuration(),
                request.getLearningObjective().getDescription()
        );
    }

    public String createPrompt(RoadmapRequest request) {
        return String.format(
                "대분류: %s\n" +
                        "중분류: %s\n" +
                        "현재 수준: %s\n" +
                        "기간: %s주\n" +
                        "학습 목표: %s\n\n" +
                        "위의 정보를 기반으로 주차별 키워드와 일별 키워드를 포함한 로드맵을 작성해주세요.",
                request.getMainCategory(),
                request.getSubCategory(),
                request.getCurrentLevel(),
                request.getDuration(),
                request.getLearningObjective().getDescription()
        );
    }

    public List<ChatMessage> buildMessages(String systemPrompt, String userPrompt) {
        return List.of(
                new ChatMessage("system", systemPrompt),
                new ChatMessage("user", userPrompt)
        );
    }
}