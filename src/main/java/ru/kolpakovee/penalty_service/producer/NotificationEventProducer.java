package ru.kolpakovee.penalty_service.producer;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import ru.kolpakovee.penalty_service.clients.UserServiceClient;
import ru.kolpakovee.penalty_service.enums.NotificationCategory;
import ru.kolpakovee.penalty_service.records.ApartmentInfo;
import ru.kolpakovee.penalty_service.records.NotificationEvent;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class NotificationEventProducer {

    @Value("${spring.kafka.topic}")
    private String topic;

    private final KafkaTemplate<String, NotificationEvent> kafkaTemplate;

    private final UserServiceClient userServiceClient;

    public void send(UUID userId, String message) {
        NotificationEvent event = NotificationEvent.builder()
                .category(NotificationCategory.PENALTY)
                .message(message)
                .userId(userId)
                .timestamp(LocalDateTime.now())
                .build();

        kafkaTemplate.send(topic, event);
    }

    public void sendToAllApartmentUsers(String message) {
        ApartmentInfo apartmentInfo = userServiceClient.getApartmentByToken();

        apartmentInfo.users().stream()
                .map(user -> NotificationEvent.builder()
                        .category(NotificationCategory.PENALTY)
                        .message(message)
                        .userId(user.id())
                        .timestamp(LocalDateTime.now())
                        .build())
                .forEach(event -> kafkaTemplate.send(topic, event));

    }
}
