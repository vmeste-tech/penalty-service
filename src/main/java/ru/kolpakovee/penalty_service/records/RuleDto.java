package ru.kolpakovee.penalty_service.records;


import ru.kolpakovee.penalty_service.enums.RuleStatus;

import java.util.UUID;

public record RuleDto(
        UUID id,
        String name,
        String description,
        RuleStatus status,
        double penaltyAmount,
        String cronExpression,
        String timeZone
) {
}
