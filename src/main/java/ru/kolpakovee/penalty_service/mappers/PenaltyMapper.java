package ru.kolpakovee.penalty_service.mappers;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;
import ru.kolpakovee.penalty_service.entities.PenaltyEntity;
import ru.kolpakovee.penalty_service.records.PenaltyDto;

@Mapper
public interface PenaltyMapper {

    PenaltyMapper INSTANCE = Mappers.getMapper(PenaltyMapper.class);

    PenaltyDto toDto(PenaltyEntity entity);
}
