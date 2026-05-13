package com.boilerplate.platform.notification.dto;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentEvent(
        UUID paymentId,
        BigDecimal amount,
        String currency,
        String customerEmail,
        String status,
        Instant createdAt
) {
}
