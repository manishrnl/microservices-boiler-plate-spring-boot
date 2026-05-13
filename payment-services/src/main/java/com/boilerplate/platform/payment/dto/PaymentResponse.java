package com.boilerplate.platform.payment.dto;

import com.boilerplate.platform.payment.entity.PaymentStatus;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

public record PaymentResponse(
        UUID id,
        BigDecimal amount,
        String currency,
        String customerEmail,
        String description,
        PaymentStatus status,
        Instant createdAt
) {
}
