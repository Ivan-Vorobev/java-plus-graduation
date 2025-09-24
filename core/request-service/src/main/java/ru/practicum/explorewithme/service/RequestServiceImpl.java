package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.*;
import ru.practicum.explorewithme.enumeration.EventRequestCountAction;
import ru.practicum.explorewithme.enumeration.EventState;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.feign.event.EventFeign;
import ru.practicum.explorewithme.feign.user.UserFeign;
import ru.practicum.explorewithme.mapper.EventRequestMapper;
import ru.practicum.explorewithme.mapper.ParticipationRequestMapper;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.RequestStatus;
import ru.practicum.explorewithme.repository.RequestRepository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static ru.practicum.explorewithme.exception.NotFoundException.notFoundException;

@Slf4j
@Service
@RequiredArgsConstructor
public class RequestServiceImpl implements RequestService {
    private final RequestRepository requestRepository;
    private final EventFeign eventFeign;
    private final UserFeign userFeign;

    @Override
    public List<ParticipationRequestDto> findAllRequestsByUserId(long userId) {
        return ParticipationRequestMapper.mapToDTO(requestRepository.findParticipationRequestByRequesterId(userId));
    }

    @Override
    public ParticipationRequestDto save(long userId, long eventId) {
        EventDto event = eventFeign.findById(eventId);

        UserDto user = userFeign.findById(userId);

        checkDuplicateRequest(userId, eventId);
        checkUserIsInitiator(event, userId);
        checkParticipantLimitEqualRequestsOnEvent(event);
        checkOnNotPublishedEvent(event);

        ParticipationRequest participationRequest = ParticipationRequest.builder()
                .requesterId(user.getId())
                .eventId(event.getId())
                .status(event.getRequestModeration() && event.getParticipantLimit() != 0 ?
                        RequestStatus.PENDING : RequestStatus.CONFIRMED)
                .created(LocalDateTime.now())
                .build();

        requestRepository.save(participationRequest);

        if (participationRequest.getStatus().equals(RequestStatus.CONFIRMED)) {
            incrementParticipantCount(event.getId(), 1, EventRequestCountAction.PLUS);
        }

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    @Override
    public ParticipationRequestDto cancelRequest(long userId, long requestId) {

        ParticipationRequest participationRequest = requestRepository.findParticipationRequestByIdAndRequesterId(requestId, userId)
                .orElseThrow(notFoundException("Запрос {0} на участие пользователя {1} не найден!", requestId, userId));
        RequestStatus oldStatus = participationRequest.getStatus();
        participationRequest.setStatus(RequestStatus.CANCELED);
        requestRepository.save(participationRequest);

        if (oldStatus.equals(RequestStatus.CONFIRMED)) {
            incrementParticipantCount(participationRequest.getEventId(), 1, EventRequestCountAction.MINUS);
        }

        return ParticipationRequestMapper.mapToDTO(participationRequest);
    }

    @Override
    public List<ParticipationRequestDto> findUserRequestsOnEvent(long userId, long eventId) {
        EventDto event = eventFeign.findById(eventId);
        if (event.getInitiator().getId() != userId) {
            throw new ConflictException("User is not Initiator of Event");
        }
        return ParticipationRequestMapper.mapToDTO(
                requestRepository.findParticipationRequestByEventId(event.getId()));
    }

    @Override
    public UserParticipationRequestDto patchRequestStatus(ChangeRequestStatusDto changeRequestStatusDto, long userId, long eventId) {

        List<ParticipationRequest> confirmedRequests = new ArrayList<>();
        List<ParticipationRequest> rejectedRequests = new ArrayList<>();
        int confirmedStatusCount = 0;

        List<ParticipationRequest> requests = requestRepository.findAllById(changeRequestStatusDto.getRequestIds());
        EventDto event = eventFeign.findById(eventId);

        long allRequestsOnEventCount = requestRepository.countParticipationRequestByEventIdAndStatus(eventId, RequestStatus.CONFIRMED);
        int participantLimit = event.getParticipantLimit();
        long canConfirmRequestsNumber = participantLimit == 0 ? requests.size() : participantLimit - allRequestsOnEventCount;

        if (canConfirmRequestsNumber <= 0) {
            throw new ConflictException(String.format(
                    "Событие с идентификатором: %d недоступно для добавления новых заявок.", eventId
            ));
        }

        for (ParticipationRequest participationRequest : requests) {
            // статус можно изменить только у заявок, находящихся в состоянии ожидания (Ожидается код ошибки 409)
            if (participationRequest.getStatus() != RequestStatus.PENDING) {
                throw new ConflictException("Status not pending: request id:  " + participationRequest.getId());
            }
        }

        for (ParticipationRequest request : requests) {
            if (changeRequestStatusDto.getStatus() == RequestStatus.REJECTED || canConfirmRequestsNumber <= 0) {
                request.setStatus(RequestStatus.REJECTED);
                rejectedRequests.add(request);
                continue;
            }

            request.setStatus(RequestStatus.CONFIRMED);
            confirmedRequests.add(request);
            canConfirmRequestsNumber--;
            confirmedStatusCount++;
        }

        requestRepository.saveAll(requests);

        if (confirmedStatusCount > 0) {
            incrementParticipantCount(event.getId(), confirmedStatusCount, EventRequestCountAction.PLUS);
        }

        return UserParticipationRequestDto.builder()
                .confirmedRequests(ParticipationRequestMapper.mapToDTO(confirmedRequests))
                .rejectedRequests(ParticipationRequestMapper.mapToDTO(rejectedRequests))
                .build();
    }

    @Override
    public List<EventRequestCountDto> findEventsCountByStatus(List<Long> eventIds, RequestStatus status) {
        return EventRequestMapper.mapToDTO(requestRepository.findEventsCountByStatus(eventIds, status));
    }

    @Override
    public Optional<ParticipationRequestDto> findByRequesterIdAndEventId(Long userId, Long eventId) {
        Optional<ParticipationRequest> requestOpt = requestRepository.findByRequesterIdAndEventId(userId, eventId);
        if (requestOpt.isEmpty()) {
            return Optional.empty();
        }
        ParticipationRequestDto dto = ParticipationRequestMapper.mapToDTO(requestOpt.get());
        return Optional.of(dto);
    }

    private void checkOnNotPublishedEvent(EventDto event) {
        if (EventState.valueOf(event.getState()) != EventState.PUBLISHED) {
            throw new ConflictException("Event is not published");
        }
    }

    private void checkDuplicateRequest(long userId, long eventId) {
        if (requestRepository.countParticipationRequestByRequesterIdAndEventId(userId, eventId) != 0) {
            throw new ConflictException("Duplicate request");
        }
    }

    private void checkUserIsInitiator(EventDto event, long userId) {
        if (event.getInitiator().getId() == userId) {
            throw new ConflictException("User is Initiator of Request Exception");
        }
    }

    private void checkParticipantLimitEqualRequestsOnEvent(EventDto event) {
        if (event.getParticipantLimit() == requestRepository.countParticipationRequestByEventIdAndStatus(event.getId(), RequestStatus.CONFIRMED)
                && event.getParticipantLimit() != 0) {
            throw new ConflictException("Participant limit is equal to request limit");
        }
    }

    private void incrementParticipantCount(Long eventId, Integer increment, EventRequestCountAction action) {
        try {
            eventFeign.updateUserEventRequestCount(eventId, increment, action);
        } catch (Exception ignored) {}
    }
}