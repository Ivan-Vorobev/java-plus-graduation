package ru.yandex.practicum.ewm.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.yandex.practicum.dto.CreateHitDTO;
import ru.yandex.practicum.ewm.stats.model.Hit;

@Mapper(componentModel = "spring")
public interface HitMapper {
    @Mapping(target = "id", ignore = true)
    Hit mapToHit(CreateHitDTO createHitDTO);
}
