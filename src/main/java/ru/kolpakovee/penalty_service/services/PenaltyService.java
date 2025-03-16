package ru.kolpakovee.penalty_service.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.kolpakovee.penalty_service.records.CreatePenaltyRequest;
import ru.kolpakovee.penalty_service.records.PenaltyDto;

@Service
@RequiredArgsConstructor
public class PenaltyService {
    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        throw new UnsupportedOperationException();
    }
}
