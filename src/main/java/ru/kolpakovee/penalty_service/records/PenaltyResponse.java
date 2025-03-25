package ru.kolpakovee.penalty_service.records;

import lombok.Builder;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PenaltyResponse(
        UUID id,
        UserInfoDto user,
        RuleDto rule,
        double fineAmount,
        LocalDateTime assignedDate,
        PaymentStatus status

) {
}
