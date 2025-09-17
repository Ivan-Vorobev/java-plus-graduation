package ru.practicum.explorewithme.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.enumeration.EventRequestCountAction;
import ru.practicum.explorewithme.events.service.EventService;

@RestController
@RequestMapping(path = "/internal/events")
@RequiredArgsConstructor
public class InternalEventsController {
    private final EventService eventService;

    @GetMapping("/{eventId}")
    public EventDto updateUserEvent(
            @Valid @NotNull @PathVariable Long eventId
    ) {
        return eventService.findEvent(eventId);
    }

    @PostMapping("/{eventId}/participant-count/{increment}")
    public void updateUserEventRequestCount(
            @Valid @NotNull @PathVariable Long eventId,
            @Valid @NotNull @PathVariable Integer increment,
            @RequestParam(required = false, defaultValue = "PLUS") EventRequestCountAction action
    ) {
        eventService.incrementParticipantCnt(eventId, increment, action);
    }
}
