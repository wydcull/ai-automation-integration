package org.wydcull.ai_chatbot_project;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableCaching
@EnableScheduling  // NEW: Enable scheduled tasks
@EnableAsync       // NEW: Enable async processing
public class AiChatbotProjectApplication {

    public static void main(String[] args) {
        SpringApplication.run(AiChatbotProjectApplication.class, args);
    }
}