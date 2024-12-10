package com.study.moya.ai_roadmap.util;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WorksheetResponseParser {

    private static final Pattern DAY_PATTERN = Pattern.compile(
            "===\\s*DAY\\s*(\\d+)\\s*===\\s*([\\s\\S]*?)(?====\\s*DAY|$)");

    public Map<Integer, String> parseResponse(String apiResponse) {
        try {
            Map<Integer, String> dayWorksheets = new HashMap<>();

            // 특수 문자 및 불필요한 포맷 제거
            apiResponse = cleanResponse(apiResponse);

            Matcher matcher = DAY_PATTERN.matcher(apiResponse);
            while (matcher.find()) {
                int day = Integer.parseInt(matcher.group(1));
                String content = matcher.group(2).trim();

                // 내용이 의미 있는 길이를 가지고 있는지 확인
                if (content.length() > 50) {  // 최소 길이 체크
                    dayWorksheets.put(day, content);
                    log.debug("Day {} 학습 가이드 파싱 완료 (길이: {})", day, content.length());
                } else {
                    log.warn("Day {} 학습 가이드 내용이 너무 짧습니다: {}", day, content);
                }
            }

            if (dayWorksheets.isEmpty()) {
                throw new IllegalStateException("파싱된 학습 가이드가 없습니다. 원본 응답: " + apiResponse);
            }

            return dayWorksheets;

        } catch (Exception e) {
            log.error("학습 가이드 파싱 중 오류 발생: {}", apiResponse, e);
            throw new RuntimeException("학습 가이드 파싱 실패", e);
        }
    }

    private String cleanResponse(String response) {
        // 마크다운 코드 블록 제거
        response = response.replaceAll("```.*?```", "");
        // 마크다운 헤더 제거
        response = response.replaceAll("#+ ", "");
        // 불릿 포인트 제거
        response = response.replaceAll("^[-*] ", "");
        // 번호 매기기 제거
        response = response.replaceAll("^\\d+\\. ", "");
        // 여러 줄 공백을 한 줄로
        response = response.replaceAll("\\n\\s*\\n", "\n");

        return response.trim();
    }
}