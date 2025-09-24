package ru.practicum.explorewithme.events.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.UserEventParams;
import ru.practicum.explorewithme.events.service.EventService;
import ru.yandex.ewm.CollectorClient;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final EventService eventService;
    private final CollectorClient collectorClient;

    @GetMapping
    public List<EventDto> getEvents(
            @RequestParam(required = false) String text,
            @RequestParam(required = false) List<Long> categories,
            @RequestParam(required = false) Boolean paid,
            @RequestParam(required = false) Boolean onlyAvailable,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeStart,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") LocalDateTime rangeEnd,
            @RequestParam Optional<UserEventParams.EventSortEnum> sort,
            @RequestParam(value = "from", defaultValue = "0") Integer from,
            @RequestParam(value = "to", defaultValue = "10") Integer size
    ) {
        UserEventParams userEventParams = UserEventParams
                .builder()
                .text(text)
                .categories(categories)
                .paid(paid)
                .onlyAvailable(onlyAvailable)
                .rangeStart(rangeStart)
                .rangeEnd(rangeEnd)
                .sort(sort)
                .from(from)
                .size(size)
                .build();

        return eventService.findAllByUserParams(userEventParams);
    }

    @GetMapping("/{eventId}")
    public EventDto getPublishedEvent(
            @NotNull @PathVariable Long eventId,
            @RequestHeader("X-EWM-USER-ID") long userId
    ) {
        collectorClient.sendView(userId, eventId);
        return eventService.findPublishedEvent(eventId);
    }

    @GetMapping("/recommendations")
    public List<EventDto> getRecommendations(@RequestHeader("X-EWM-USER-ID") long userId, @RequestParam int maxResults) {
        return eventService.getRecommendations(userId, maxResults);
    }

    @PutMapping("/{eventId}/like")
    public void likeEvent(@PathVariable long eventId, @RequestHeader("X-EWM-USER-ID") long userId) {
        eventService.likeEvent(userId, eventId);
    }
}
