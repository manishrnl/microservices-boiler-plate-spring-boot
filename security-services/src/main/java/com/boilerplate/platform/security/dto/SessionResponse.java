package com.boilerplate.platform.security.dto;

import java.time.Instant;
import java.util.UUID;

public record SessionResponse(
        UUID sessionId,
        String deviceName,
        Instant loggedInAt,
        Instant lastSeenAt
) {
}
