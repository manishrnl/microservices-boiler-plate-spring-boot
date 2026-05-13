package com.boilerplate.platform.payment.service;

import com.boilerplate.platform.payment.dto.PaymentEvent;
import com.boilerplate.platform.payment.dto.PaymentRequest;
import com.boilerplate.platform.payment.dto.PaymentResponse;
import com.boilerplate.platform.payment.entity.Payment;
import com.boilerplate.platform.payment.repository.PaymentRepository;
import java.util.List;
import java.util.UUID;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KafkaTemplate<String, PaymentEvent> kafkaTemplate;
    private final String paymentTopic;

    public PaymentService(
            PaymentRepository paymentRepository,
            KafkaTemplate<String, PaymentEvent> kafkaTemplate,
            @Value("${app.kafka.topics.payment-events}") String paymentTopic
    ) {
        this.paymentRepository = paymentRepository;
        this.kafkaTemplate = kafkaTemplate;
        this.paymentTopic = paymentTopic;
    }

    @Transactional
    public PaymentResponse createPayment(PaymentRequest request) {
        Payment payment = new Payment(
                request.amount(),
                request.currency().toUpperCase(),
                request.customerEmail(),
                request.description()
        );
        payment.complete();
        Payment saved = paymentRepository.save(payment);

        kafkaTemplate.send(paymentTopic, saved.getId().toString(), toEvent(saved));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public List<PaymentResponse> listPayments() {
        return paymentRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public PaymentResponse getPayment(UUID id) {
        return paymentRepository.findById(id)
                .map(this::toResponse)
                .orElseThrow(() -> new IllegalArgumentException("Payment not found: " + id));
    }

    private PaymentResponse toResponse(Payment payment) {
        return new PaymentResponse(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCustomerEmail(),
                payment.getDescription(),
                payment.getStatus(),
                payment.getCreatedAt()
        );
    }

    private PaymentEvent toEvent(Payment payment) {
        return new PaymentEvent(
                payment.getId(),
                payment.getAmount(),
                payment.getCurrency(),
                payment.getCustomerEmail(),
                payment.getStatus().name(),
                payment.getCreatedAt()
        );
    }
}
