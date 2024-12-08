package com.study.moya.ai_roadmap.config;


import com.theokanning.openai.service.OpenAiService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAIConfig {

    @Value("${openai.api.models.roadmap_generation.model}")
    private String roadmapModel;

    @Value("${openai.api.models.category_classification.model}")
    private String searchModel;

    @Bean
    public OpenAiService openAiService(@Value("${openai.api.key}") String apiKey) {
        return new OpenAiService(apiKey, Duration.ofSeconds(300));
    }
}