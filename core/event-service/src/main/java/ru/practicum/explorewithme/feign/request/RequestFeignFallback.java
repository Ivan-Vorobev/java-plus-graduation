package ru.practicum.explorewithme.feign.request;


import org.springframework.stereotype.Component;
import ru.practicum.explorewithme.feign.dto.EventRequestCountDto;
import ru.practicum.explorewithme.feign.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.feign.enumeration.RequestStatus;

import java.util.List;
import java.util.Optional;

@Component
public class RequestFeignFallback implements RequestFeign {
    @Override
    public List<EventRequestCountDto> findEventsCountByStatus(List<Long> eventIds, RequestStatus status) {
        return List.of();
    }

    @Override
    public Optional<ParticipationRequestDto> findByRequesterIdAndEventId(Long userId, Long eventId) {
        return Optional.empty();
    }
}
