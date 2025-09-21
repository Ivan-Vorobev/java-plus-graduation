package ru.practicum.explorewithme.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.enumeration.RequestStatus;

import java.time.LocalDateTime;

@Data
@Builder
public class ParticipationRequestDto {
    private Long id;
    private Long event;
    private Long requester;
    private LocalDateTime created;
    private RequestStatus status;
}