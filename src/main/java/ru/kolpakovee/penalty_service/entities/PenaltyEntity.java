package ru.kolpakovee.penalty_service.entities;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.kolpakovee.penalty_service.enums.PaymentStatus;

import java.time.LocalDateTime;
import java.util.UUID;

@Setter
@Getter
@Entity
@Table(name = "penalties")
public class PenaltyEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "apartment_id", nullable = false)
    private UUID apartmentId;

    @Column(name = "assigned_to", nullable = false)
    private UUID assignedTo;

    @Column(name = "rule_id", nullable = false)
    private UUID ruleId;

    @Column(name = "fine_amount", nullable = false)
    private double fineAmount;

    @Column(name = "assigned_date", nullable = false)
    private LocalDateTime assignedDate;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private PaymentStatus status;
}
