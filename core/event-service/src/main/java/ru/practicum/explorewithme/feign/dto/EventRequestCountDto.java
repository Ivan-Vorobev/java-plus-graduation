package ru.practicum.explorewithme.feign.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class EventRequestCountDto {
    @NotNull
    private Long eventId;
    @NotNull
    private Long requestsCount;
}
