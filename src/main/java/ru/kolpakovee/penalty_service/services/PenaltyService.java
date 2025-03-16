package ru.kolpakovee.penalty_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.penalty_service.clients.UserServiceClient;
import ru.kolpakovee.penalty_service.entities.PenaltyEntity;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;
import ru.kolpakovee.penalty_service.mappers.PenaltyMapper;
import ru.kolpakovee.penalty_service.records.CreatePenaltyRequest;
import ru.kolpakovee.penalty_service.records.PenaltyDto;
import ru.kolpakovee.penalty_service.repositories.PenaltyRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PenaltyService {

    private final PenaltyRepository penaltyRepository;
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

    public List<PenaltyDto> getApartmentPenalties(LocalDateTime start, LocalDateTime end) {
        if (start != null && end != null && start.isAfter(end)) {
            throw new IllegalArgumentException("Некорректный временной интервал");
        }

        UUID apartmentId = userServiceClient.getApartmentByToken().apartmentId();

        return penaltyRepository.findByApartmentIdAndPeriod(apartmentId, start, end).stream()
                .map(PenaltyMapper.INSTANCE::toDto)
                .toList();
    }
}
