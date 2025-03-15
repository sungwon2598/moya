package com.study.moya.ai_roadmap.service;

import com.study.moya.ai_roadmap.domain.DailyPlan;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorksheetPromptService {

    private static final String SYSTEM_PROMPT = """
        당신은 프로그래밍 교육 전문가입니다. 주어진 일차의 학습 키워드에 대해 상세한 학습 가이드를 작성해주세요.
        
        응답은 반드시 아래 형식을 따라주세요. 절대로 마크다운이나 특수문자, 번호 매기기를 사용하지 마세요.
        
        %s
        
        각 일자별 학습 가이드는 다음 내용을 자연스러운 문장으로 포함해야 합니다:
        - 해당 주제의 중요성
        - 핵심 학습 내용
        - 실제 활용 방법
        - 실습 예제나 실무 활용 사례
        - 학습 시 주의사항
        
        특별 지시사항:
        - 번호나 불릿 포인트(-, * 등)를 사용하지 마세요
        - 마크다운 형식(#, ``` 등)을 사용하지 마세요
        - JSON 형식을 사용하지 마세요
        - 각 일자는 반드시 === DAY {일차} === 형식으로 구분해주세요
        """;

    public String createPrompt(List<DailyPlan> dailyPlans) {

        StringBuilder dayFormatBuilder = new StringBuilder();
        for (DailyPlan plan : dailyPlans) {
            dayFormatBuilder.append(String.format("=== DAY %d ===\n[여기에 학습 가이드 내용을 자연스러운 문장으로 작성]\n\n", plan.getDayNumber()));
        }
        String dayFormat = dayFormatBuilder.toString().trim();

        String systemPrompt = String.format(SYSTEM_PROMPT, dayFormat);

        StringBuilder promptBuilder = new StringBuilder();
        promptBuilder.append(systemPrompt).append("\n\n");
        promptBuilder.append("다음 키워드들에 대한 학습 가이드를 작성해주세요:\n");

        for (DailyPlan plan : dailyPlans) {
            promptBuilder.append(String.format("Day %d: %s\n", plan.getDayNumber(), plan.getKeyword()));
        }

        return promptBuilder.toString();
    }
}