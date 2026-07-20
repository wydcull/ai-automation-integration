package com.firstaiAutomationSystem.project.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class OpenAiConfig {

    @Bean(destroyMethod = "close")
    public OpenAIClient openAIClient(@Value("${groq.api.key}") String apiKey) {
        return OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .baseUrl("https://api.groq.com/openai/v1/")
                .timeout(Duration.ofSeconds(60))
                .build();
    }
}