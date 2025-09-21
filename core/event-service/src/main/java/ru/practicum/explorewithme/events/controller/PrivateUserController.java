package ru.practicum.explorewithme.events.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.dto.RequestMethod;
import ru.practicum.explorewithme.events.service.EventService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private final EventService eventService;

    @GetMapping("/{userId}/events")
    public List<EventDto> getEvents(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @RequestParam(value = "from", defaultValue = "0") Integer from,
            @Valid @RequestParam(value = "to", defaultValue = "10") Integer to
    ) {

        return eventService.findAllByParams(userId, from, to);
    }

    @PostMapping("/{userId}/events")
    @ResponseStatus(HttpStatus.CREATED)
    public EventDto createEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Validated({RequestMethod.Create.class}) @RequestBody NewEventDto eventDto
    ) {
        return eventService.addEvent(eventDto, userId);
    }

    @GetMapping("/{userId}/events/{eventId}")
    public EventDto getUserEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @NotNull @PathVariable Long eventId
    ) {

        return eventService.findUserEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}")
    public EventDto updateUserEvent(
            @Valid @NotNull @PathVariable Long userId,
            @Valid @NotNull @PathVariable Long eventId,
            @Validated({RequestMethod.Update.class}) @RequestBody NewEventDto eventDto
    ) {

        return eventService.updateEventByUser(eventId, eventDto, userId);
    }
}