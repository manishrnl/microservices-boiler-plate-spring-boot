package com.boilerplate.platform.notification.service;

import com.boilerplate.platform.notification.dto.NotificationResponse;
import com.boilerplate.platform.notification.dto.PaymentEvent;
import com.boilerplate.platform.notification.entity.Notification;
import com.boilerplate.platform.notification.repository.NotificationRepository;
import java.util.List;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;

    public NotificationService(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;
    }

    @KafkaListener(
            topics = "${app.kafka.topics.payment-events}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    @Transactional
    public void handlePaymentEvent(PaymentEvent event) {
        String message = "Payment " + event.paymentId() + " is " + event.status()
                + " for " + event.amount() + " " + event.currency() + ".";
        Notification notification = new Notification(
                event.paymentId(),
                event.customerEmail(),
                "Payment " + event.status(),
                message
        );
        notificationRepository.save(notification);
    }

    @Transactional(readOnly = true)
    public List<NotificationResponse> listNotifications() {
        return notificationRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    private NotificationResponse toResponse(Notification notification) {
        return new NotificationResponse(
                notification.getId(),
                notification.getPaymentId(),
                notification.getRecipient(),
                notification.getSubject(),
                notification.getMessage(),
                notification.getCreatedAt()
        );
    }
}
