package ru.kolpakovee.penalty_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.kolpakovee.penalty_service.records.CreatePenaltyRequest;
import ru.kolpakovee.penalty_service.records.PenaltyDto;
import ru.kolpakovee.penalty_service.services.PenaltyService;

@RestController
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @PostMapping
    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        return penaltyService.createPenalty(request);
    }
}
