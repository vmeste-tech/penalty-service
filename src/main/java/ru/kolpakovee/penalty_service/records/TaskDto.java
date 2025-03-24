package ru.kolpakovee.penalty_service.records;

import lombok.Builder;
import ru.kolpakovee.penalty_service.enums.TaskStatus;

import java.time.ZonedDateTime;
import java.util.UUID;

@Builder
public record TaskDto(
        UUID id,
        String title,
        String description,
        TaskStatus status,
        ZonedDateTime scheduledAt,
        UUID apartmentId,
        UUID assignedTo,
        UUID ruleId,
        boolean isPenaltyCreated
) {
}
