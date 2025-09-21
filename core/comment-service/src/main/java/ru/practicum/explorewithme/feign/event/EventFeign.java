package ru.practicum.explorewithme.feign.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.dto.EventDto;

@FeignClient(name = "event-service")
public interface EventFeign {
    @GetMapping("/internal/events/{eventId}")
    EventDto findById(@PathVariable(name = "eventId") Long eventId);
}
