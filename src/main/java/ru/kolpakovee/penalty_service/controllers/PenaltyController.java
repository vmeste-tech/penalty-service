package ru.kolpakovee.penalty_service.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.penalty_service.records.CreatePenaltyRequest;
import ru.kolpakovee.penalty_service.records.PenaltyDto;
import ru.kolpakovee.penalty_service.services.PenaltyService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
public class PenaltyController {

    private final PenaltyService penaltyService;

    @PostMapping
    public PenaltyDto createPenalty(CreatePenaltyRequest request) {
        return penaltyService.createPenalty(request);
    }

    @GetMapping
    public List<PenaltyDto> getApartmentPenalties(@RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                  @RequestParam(required = false)
                                                  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        LocalDateTime start = startDate != null ? startDate.atStartOfDay() : null;
        LocalDateTime end = endDate != null ? endDate.atTime(23, 59, 59) : null;

        return penaltyService.getApartmentPenalties(start, end);
    }
}
