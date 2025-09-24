package ru.practicum.explorewithme.events.service;

import ru.practicum.explorewithme.events.dto.AdminEventParams;
import ru.practicum.explorewithme.events.dto.EventDto;
import ru.practicum.explorewithme.events.dto.NewEventDto;
import ru.practicum.explorewithme.events.dto.UserEventParams;
import ru.practicum.explorewithme.events.enumeration.EventRequestCountAction;
import ru.practicum.explorewithme.events.model.Event;

import java.util.List;

public interface EventService {
    EventDto addEvent(NewEventDto newEventDto, Long userId);

    EventDto updateEventByUser(Long eventId, NewEventDto newEventDto, Long userId);

    EventDto updateEventByAdmin(Long eventId, NewEventDto newEventDto);

    List<EventDto> findAllByParams(Long userId, Integer from, Integer to);

    EventDto findUserEvent(Long userId, Long eventId);

    Event findEventById(Long eventId);
    EventDto findEvent(Long eventId);

    List<EventDto> findAllByAdminParams(AdminEventParams adminEventParams);

    List<EventDto> findAllByUserParams(UserEventParams userEventParams);

    EventDto findPublishedEvent(Long eventId);

    void incrementParticipantCnt(Long eventId, Integer increment, EventRequestCountAction action);

    List<EventDto> getRecommendations(long userId, int maxResults);

    void likeEvent(Long userId, Long eventId);
}
