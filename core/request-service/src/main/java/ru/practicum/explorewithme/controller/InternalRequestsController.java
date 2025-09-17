package ru.practicum.explorewithme.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.EventRequestCountDto;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.service.RequestService;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/internal/requests")
@RequiredArgsConstructor
public class InternalRequestsController {
    private final RequestService requestService;

    @GetMapping("/events-count")
    @ResponseStatus(HttpStatus.OK)
    public List<EventRequestCountDto> findEventsCountByStatus(
            @RequestParam("eventIds") @NotNull List<Long> eventIds,
            @RequestParam("status") @NotNull RequestStatus status
    ) {
        return requestService.findEventsCountByStatus(eventIds, status);
    }
}
