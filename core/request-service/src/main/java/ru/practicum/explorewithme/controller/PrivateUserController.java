package ru.practicum.explorewithme.controller;

import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.ChangeRequestStatusDto;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.UserParticipationRequestDto;
import ru.practicum.explorewithme.service.RequestService;
import ru.yandex.ewm.CollectorClient;

import java.util.List;
import java.util.Optional;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private final RequestService requestService;
    private final CollectorClient collectorClient;

    @GetMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.OK)
    public List<ParticipationRequestDto> findAllRequests(@PathVariable long userId) {
        log.info("Выполняется получение всех запросов пользователя: {}", userId);
        return requestService.findAllRequestsByUserId(userId);
    }

    @PostMapping("/{userId}/requests")
    @ResponseStatus(HttpStatus.CREATED)
    public ParticipationRequestDto save(@PathVariable long userId,
                                        @RequestParam("eventId") @NotNull long eventId) {
        log.info("Выполняется добавление запроса от пользователя {} на участие в событии: {}", userId, eventId);
        ParticipationRequestDto result = requestService.save(userId, eventId);
        collectorClient.sendRegistration(userId, eventId);
        return result;
    }

    @PatchMapping("/{userId}/requests/{requestId}/cancel")
    @ResponseStatus(HttpStatus.OK)
    public ParticipationRequestDto cancelRequest(@PathVariable long userId,
                                                 @PathVariable long requestId) {
        log.info("Выполняется отмена запроса пользователя {} на участие по запросу: {}", userId, requestId);
        return requestService.cancelRequest(userId, requestId);
    }

    @GetMapping("/{userId}/events/{eventId}/requests")
    public List<ParticipationRequestDto> findUserRequestsOnEvent(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.findUserRequestsOnEvent(userId, eventId);
    }

    @PatchMapping("/{userId}/events/{eventId}/requests")
    public UserParticipationRequestDto patchRequestStatus(@RequestBody ChangeRequestStatusDto changeRequestStatusDto,
                                                          @PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.patchRequestStatus(changeRequestStatusDto, userId, eventId);
    }

    @GetMapping("/users/{userId}/events/{eventId}/requests/requester")
    public Optional<ParticipationRequestDto> findByRequesterIdAndEventId(@PathVariable Long userId, @PathVariable Long eventId) {
        return requestService.findByRequesterIdAndEventId(userId, eventId);
    }
}