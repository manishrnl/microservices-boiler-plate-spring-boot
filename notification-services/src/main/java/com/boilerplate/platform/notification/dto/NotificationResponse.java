package com.boilerplate.platform.notification.dto;

import java.time.Instant;
import java.util.UUID;

public record NotificationResponse(
        UUID id,
        UUID paymentId,
        String recipient,
        String subject,
        String message,
        Instant createdAt
) {
}
