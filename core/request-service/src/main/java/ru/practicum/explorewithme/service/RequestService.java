package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.ChangeRequestStatusDto;
import ru.practicum.explorewithme.dto.EventRequestCountDto;
import ru.practicum.explorewithme.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.dto.UserParticipationRequestDto;
import ru.practicum.explorewithme.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestService {
    List<ParticipationRequestDto> findAllRequestsByUserId(long userId);

    ParticipationRequestDto save(long userId, long eventId);

    ParticipationRequestDto cancelRequest(long userId, long eventId);

    List<ParticipationRequestDto> findUserRequestsOnEvent(long userId, long eventId);

    UserParticipationRequestDto patchRequestStatus(ChangeRequestStatusDto changeRequestStatusDto, long userId, long eventId);

    List<EventRequestCountDto> findEventsCountByStatus(List<Long> eventIds, RequestStatus status);

    Optional<ParticipationRequestDto> findByRequesterIdAndEventId(Long userId, Long eventId);
}
