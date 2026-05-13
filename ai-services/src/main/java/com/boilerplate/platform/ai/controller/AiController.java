package com.boilerplate.platform.ai.controller;

import com.boilerplate.platform.ai.dto.ChatRequest;
import com.boilerplate.platform.ai.dto.ChatResponse;
import com.boilerplate.platform.ai.service.AiChatService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiChatService aiChatService;

    public AiController(AiChatService aiChatService) {
        this.aiChatService = aiChatService;
    }

    @GetMapping("/health")
    public Map<String, String> health() {
        return Map.of("service", "ai-services", "status", "UP");
    }

    @PostMapping("/chat")
    public ChatResponse chat(@Valid @RequestBody ChatRequest request) {
        return aiChatService.chat(request.prompt());
    }
}
