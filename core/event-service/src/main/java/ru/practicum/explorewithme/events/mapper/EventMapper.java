package ru.practicum.explorewithme.events.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.model.Event;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EventMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(source = "location.lat", target = "locationLat")
    @Mapping(source = "location.lon", target = "locationLon")
    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "initiator.id", target = "initiatorId")
    Event toModel(EventDto eventDto);

    @Mapping(source = "category", target = "category.id")
    EventDto convertShortDto(NewEventDto eventDto);

    @Mapping(source = "locationLat", target = "location.lat")
    @Mapping(source = "locationLon", target = "location.lon")
    @Mapping(source = "categoryId", target = "category.id")
    @Mapping(source = "initiatorId", target = "initiator.id")
    EventDto toDto(Event event);

    List<EventDto> toDto(List<Event> events);
}
