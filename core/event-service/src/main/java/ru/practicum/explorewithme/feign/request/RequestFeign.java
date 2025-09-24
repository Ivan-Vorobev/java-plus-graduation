package ru.practicum.explorewithme.feign.request;

import jakarta.validation.constraints.NotNull;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Primary;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import ru.practicum.explorewithme.feign.dto.EventRequestCountDto;
import ru.practicum.explorewithme.feign.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.feign.enumeration.RequestStatus;

import java.util.List;
import java.util.Optional;

@Primary
@FeignClient(name = "request-service", fallback = RequestFeignFallback.class)
public interface RequestFeign {
    @GetMapping("/internal/requests/events-count")
    @ResponseStatus(HttpStatus.OK)
    List<EventRequestCountDto> findEventsCountByStatus(
            @RequestParam("eventIds") @NotNull List<Long> eventIds,
            @RequestParam("status") @NotNull RequestStatus status
    );

    @GetMapping("/users/{userId}/events/{eventId}/requests/requester")
    Optional<ParticipationRequestDto> findByRequesterIdAndEventId(@PathVariable Long userId, @PathVariable Long eventId);
}
