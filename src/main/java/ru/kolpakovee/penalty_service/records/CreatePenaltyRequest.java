package ru.kolpakovee.penalty_service.records;

import java.util.UUID;

public record CreatePenaltyRequest(
        UUID apartmentId,
        UUID assignedTo,
        UUID ruleId,
        double fineAmount
) {
}
