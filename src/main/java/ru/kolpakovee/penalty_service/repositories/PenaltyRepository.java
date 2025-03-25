package ru.kolpakovee.penalty_service.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import ru.kolpakovee.penalty_service.entities.PenaltyEntity;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Repository
public interface PenaltyRepository extends JpaRepository<PenaltyEntity, UUID> {

    @Query("SELECT e FROM PenaltyEntity e WHERE " +
            "e.apartmentId = :apartmentId AND " +
            "(CAST(:start AS timestamp) IS NULL OR e.assignedDate >= :start) AND " +
            "(CAST(:end AS timestamp) IS NULL OR e.assignedDate <= :end)")
    List<PenaltyEntity> findByApartmentIdAndPeriod(
            @Param("apartmentId") UUID apartmentId,
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end
    );

    List<PenaltyEntity> findAllByApartmentId(UUID apartmentId);
}
