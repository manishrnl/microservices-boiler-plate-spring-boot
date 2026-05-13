package com.boilerplate.platform.ai.service;

import com.boilerplate.platform.ai.dto.ChatResponse;
import java.time.Instant;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class AiChatService {

    private final ChatClient chatClient;
    private final String model;

    public AiChatService(ChatClient.Builder chatClientBuilder, @Value("${spring.ai.ollama.chat.model}") String model) {
        this.chatClient = chatClientBuilder.build();
        this.model = model;
    }

    public ChatResponse chat(String prompt) {
        String answer = chatClient.prompt()
                .system("You are a concise assistant inside a reusable microservices boilerplate.")
                .user(prompt)
                .call()
                .content();
        return new ChatResponse(answer, model, Instant.now());
    }
}
