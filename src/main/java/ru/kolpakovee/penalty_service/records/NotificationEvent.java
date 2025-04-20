package ru.kolpakovee.penalty_service.records;

import lombok.Builder;
import ru.kolpakovee.penalty_service.enums.NotificationCategory;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record NotificationEvent(
        UUID userId,
        String message,
        NotificationCategory category,
        LocalDateTime timestamp
) {
}
