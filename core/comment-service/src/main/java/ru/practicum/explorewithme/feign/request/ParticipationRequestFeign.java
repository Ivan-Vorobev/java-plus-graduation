package ru.practicum.explorewithme.feign.request;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;

import java.util.List;

@FeignClient(name = "request-service")
public interface ParticipationRequestFeign {
    @GetMapping("/{userId}/events/{eventId}/requests")
    List<ParticipationRequestDto> findUserRequestsOnEvent(@PathVariable Long userId, @PathVariable Long eventId);

}
