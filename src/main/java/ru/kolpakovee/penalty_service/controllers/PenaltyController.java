package ru.kolpakovee.penalty_service.controllers;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;
import ru.kolpakovee.penalty_service.records.CreatePenaltyRequest;
import ru.kolpakovee.penalty_service.records.PenaltyDto;
import ru.kolpakovee.penalty_service.records.PenaltyResponse;
import ru.kolpakovee.penalty_service.services.PenaltyService;

import java.util.List;
import java.util.UUID;

@CrossOrigin
@RestController
@RequestMapping("/api/v1/penalties")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Управление штрафами", description = "API для управления штрафами")
public class PenaltyController {

    private final PenaltyService penaltyService;

    @PostMapping
    @Operation(summary = "Создание штрафа",
            description = "Позволяет создать новый штраф")
    public PenaltyDto createPenalty(@RequestBody CreatePenaltyRequest request) {
        return penaltyService.createPenalty(request);
    }

    @GetMapping("/{apartmentId}")
    @Operation(summary = "Получение штрафов по квартире",
            description = "Позволяет получить все штрафы по идентификатору квартиры")
    public List<PenaltyResponse> getApartmentPenalties(@PathVariable UUID apartmentId) {
        return penaltyService.getApartmentPenalties(apartmentId);
    }

    @PatchMapping("/{penaltyId}")
    @Operation(summary = "Изменение статуса штрафа",
            description = "Позволяет изменить статус штрафа по идентификатору")
    public PenaltyDto changeStatus(@PathVariable UUID penaltyId, @RequestParam PaymentStatus status) {
        return penaltyService.changeStatus(penaltyId, status);
    }

    @DeleteMapping("/{penaltyId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @Operation(summary = "Удаление штрафа",
            description = "Позволяет удалить штраф по идентификатору")
    public void deletePenalty(@PathVariable UUID penaltyId) {
        penaltyService.deletePenalty(penaltyId);
    }
}
