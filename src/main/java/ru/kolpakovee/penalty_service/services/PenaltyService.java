package ru.kolpakovee.penalty_service.services;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.penalty_service.clients.RulesServiceClient;
import ru.kolpakovee.penalty_service.clients.TaskServiceClient;
import ru.kolpakovee.penalty_service.clients.UserServiceClient;
import ru.kolpakovee.penalty_service.constants.NotificationMessages;
import ru.kolpakovee.penalty_service.entities.PenaltyEntity;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;
import ru.kolpakovee.penalty_service.enums.RuleStatus;
import ru.kolpakovee.penalty_service.exceptions.NotFoundException;
import ru.kolpakovee.penalty_service.mappers.PenaltyMapper;
import ru.kolpakovee.penalty_service.producer.NotificationEventProducer;
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

    private final NotificationEventProducer producer;

    @Transactional
    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        PenaltyEntity penaltyEntity = new PenaltyEntity();
        penaltyEntity.setApartmentId(request.apartmentId());
        penaltyEntity.setStatus(PaymentStatus.UNPAID);
        penaltyEntity.setAssignedDate(LocalDateTime.now());
        penaltyEntity.setFineAmount(request.fineAmount());
        penaltyEntity.setRuleId(request.ruleId());
        penaltyEntity.setAssignedTo(request.assignedTo());

        producer.send(request.assignedTo(), NotificationMessages.CREATE_PENALTY);

        return PenaltyMapper.INSTANCE.toDto(penaltyRepository.save(penaltyEntity));
    }

    @Transactional
    public List<PenaltyResponse> getApartmentPenalties(UUID apartmentId) {
        Map<UUID, RuleDto> rules = rulesServiceClient.getApartmentRules(apartmentId).stream()
                .filter(r -> r.status().equals(RuleStatus.ACCEPTED))
                .collect(Collectors.toMap(RuleDto::id, ruleDto -> ruleDto));

        // Просроченные задачи
        List<TaskDto> overdueTasks = taskServiceClient.getOverdueTasks(apartmentId)
                .stream()
                .filter(t -> t.scheduledAt().isBefore(ZonedDateTime.now()))
                .filter(t -> !t.isPenaltyCreated())
                .toList();

        Map<UUID, UserInfoDto> users = userServiceClient.getApartmentByToken().users().stream()
                .collect(Collectors.toMap(UserInfoDto::id, u -> u));

        List<PenaltyEntity> existedPenalties = penaltyRepository.findAllByApartmentId(apartmentId);

        overdueTasks.forEach(t -> {
            if (!penaltyExists(existedPenalties, t.ruleId(), t.scheduledAt().toLocalDateTime())) {
                PenaltyEntity penaltyEntity = new PenaltyEntity();
                penaltyEntity.setApartmentId(apartmentId);
                penaltyEntity.setAssignedDate(t.scheduledAt().toLocalDateTime());
                penaltyEntity.setRuleId(t.ruleId());
                penaltyEntity.setFineAmount(rules.get(t.ruleId()).penaltyAmount());
                penaltyEntity.setStatus(PaymentStatus.UNPAID);
                penaltyEntity.setAssignedTo(t.assignedTo());
                taskServiceClient.changePenaltyStatus(t.id(), true);
                penaltyRepository.save(penaltyEntity);
                existedPenalties.add(penaltyEntity);
            }
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

        producer.sendToAllApartmentUsers(NotificationMessages.CHANGE_PENALTY_STATUS);

        return PenaltyMapper.INSTANCE.toDto(penaltyRepository.save(entity));
    }

    @Transactional
    public void deletePenalty(UUID penaltyId) {
        penaltyRepository.deleteById(penaltyId);
    }

    /**
     * Проверяет, существует ли уже штраф для данной задачи в указанное время.
     */
    private boolean penaltyExists(List<PenaltyEntity> existedPenalties, UUID ruleId, LocalDateTime assignedDate) {
        return existedPenalties.stream()
                .anyMatch(penalty -> penalty.getRuleId().equals(ruleId)
                        && penalty.getAssignedDate().equals(assignedDate));
    }
}
