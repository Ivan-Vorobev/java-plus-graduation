package ru.practicum.explorewithme.feign.event;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.dto.EventDto;
import ru.practicum.explorewithme.enumeration.EventRequestCountAction;

@FeignClient(name = "event-service")
public interface    EventFeign {
    @GetMapping("/internal/events/{eventId}")
    EventDto findById(@PathVariable(name = "eventId") Long eventId);

    @PostMapping("/{eventId}/participant-count/{increment}")
    EventDto updateUserEventRequestCount(
            @Valid @NotNull @PathVariable Long eventId,
            @Valid @NotNull @PathVariable Integer increment,
            @RequestParam(required = false, defaultValue = "PLUS") EventRequestCountAction action
    );
}
