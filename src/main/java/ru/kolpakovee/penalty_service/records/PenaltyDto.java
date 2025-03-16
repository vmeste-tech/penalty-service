package ru.kolpakovee.penalty_service.records;

import ru.kolpakovee.penalty_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

public record PenaltyDto(
        UUID id,
        UUID apartmentId,
        UUID assignedTo,
        UUID ruleId,
        double fineAmount,
        LocalDateTime assignedDate,
        PaymentStatus status
) {
}
