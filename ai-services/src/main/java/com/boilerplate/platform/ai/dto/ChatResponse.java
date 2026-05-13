package com.boilerplate.platform.ai.dto;

import java.time.Instant;

public record ChatResponse(
        String answer,
        String model,
        Instant createdAt
) {
}
