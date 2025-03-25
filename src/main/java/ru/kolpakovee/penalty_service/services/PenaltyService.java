package ru.kolpakovee.penalty_service.services;

import jakarta.transaction.Transactional;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.penalty_service.clients.RulesServiceClient;
import ru.kolpakovee.penalty_service.clients.TaskServiceClient;
import ru.kolpakovee.penalty_service.clients.UserServiceClient;
import ru.kolpakovee.penalty_service.entities.PenaltyEntity;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;
import ru.kolpakovee.penalty_service.enums.RuleStatus;
import ru.kolpakovee.penalty_service.exceptions.NotFoundException;
import ru.kolpakovee.penalty_service.mappers.PenaltyMapper;
import ru.kolpakovee.penalty_service.records.*;
import ru.kolpakovee.penalty_service.repositories.PenaltyRepository;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;

    private final TaskServiceClient taskServiceClient;
    private final RulesServiceClient rulesServiceClient;
    private final UserServiceClient userServiceClient;

    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        PenaltyEntity penaltyEntity = new PenaltyEntity();
        penaltyEntity.setApartmentId(request.apartmentId());
        penaltyEntity.setStatus(PaymentStatus.UNPAID);
        penaltyEntity.setAssignedDate(LocalDateTime.now());
        penaltyEntity.setFineAmount(request.fineAmount());
        penaltyEntity.setRuleId(request.ruleId());
        penaltyEntity.setAssignedTo(request.assignedTo());

        return PenaltyMapper.INSTANCE.toDto(penaltyRepository.save(penaltyEntity));
    }

    @Transactional
    public List<PenaltyResponse> getApartmentPenalties(UUID apartmentId) {
        Map<UUID, RuleDto> rules = rulesServiceClient.getApartmentRules(apartmentId).stream()
                .filter(r -> r.status().equals(RuleStatus.ACCEPTED))
                .collect(Collectors.toMap(RuleDto::id, ruleDto -> ruleDto));

        // Просроченные задачи
        List<TaskDto> tasks = taskServiceClient.getOverdueTasks(apartmentId)
                .stream()
                .filter(t -> t.scheduledAt().isBefore(ZonedDateTime.now()))
                .filter(t -> !t.isPenaltyCreated())
                .toList();

        Map<UUID, UserInfoDto> users = userServiceClient.getApartmentByToken().users().stream()
                .collect(Collectors.toMap(UserInfoDto::id, u -> u));

        tasks.forEach(t -> {
            PenaltyEntity penaltyEntity = new PenaltyEntity();
            penaltyEntity.setApartmentId(apartmentId);
            penaltyEntity.setAssignedDate(t.scheduledAt().toLocalDateTime());
            penaltyEntity.setRuleId(t.ruleId());
            penaltyEntity.setFineAmount(rules.get(t.ruleId()).penaltyAmount());
            penaltyEntity.setStatus(PaymentStatus.UNPAID);
            penaltyEntity.setAssignedTo(t.assignedTo());
            // TODO: изменить статус задачи на штраф создан
            penaltyRepository.save(penaltyEntity);
        });

        return penaltyRepository.findAllByApartmentId(apartmentId)
                .stream()
                .map(p -> PenaltyResponse.builder()
                        .id(p.getId())
                        .assignedDate(p.getAssignedDate())
                        .status(p.getStatus())
                        .fineAmount(p.getFineAmount())
                        .rule(rules.get(p.getRuleId()))
                        .user(users.get(p.getAssignedTo()))
                        .build())
                .toList();
    }

    @Transactional
    public PenaltyDto changeStatus(UUID penaltyId, PaymentStatus status) {
        PenaltyEntity entity = penaltyRepository.findById(penaltyId).orElseThrow(() ->
                new NotFoundException("Штраф для изменения статуса не найден."));

        entity.setStatus(status);

        return PenaltyMapper.INSTANCE.toDto(penaltyRepository.save(entity));
    }

    public void deletePenalty(UUID penaltyId) {
        penaltyRepository.deleteById(penaltyId);
    }
}
