package ru.practicum.explorewithme.mapper;

import ru.practicum.explorewithme.dto.EventRequestCountDto;
import ru.practicum.explorewithme.model.EventRequestCount;

import java.util.List;
import java.util.stream.Collectors;

public class EventRequestMapper {

    public static EventRequestCountDto mapToDTO(EventRequestCount eventRequestCount) {
        return EventRequestCountDto.builder()
                .eventId(eventRequestCount.getEventId())
                .requestsCount(eventRequestCount.getRequestsCount())
                .build();
    }

    public static List<EventRequestCountDto> mapToDTO(List<EventRequestCount> eventRequestCountList) {
        return eventRequestCountList.stream()
                .map(EventRequestMapper::mapToDTO)
                .collect(Collectors.toList());
    }
}
