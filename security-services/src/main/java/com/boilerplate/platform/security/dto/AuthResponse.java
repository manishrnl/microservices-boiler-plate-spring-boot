package com.boilerplate.platform.security.dto;

import java.time.Instant;
import java.util.UUID;

public record AuthResponse(
        UUID userId,
        String fullName,
        String email,
        UUID sessionId,
        String accessToken,
        String deviceName,
        int activeSessions,
        Instant loggedInAt
) {
}
