package ru.practicum.explorewithme.events.service;

import feign.FeignException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.stats.messages.RecommendedEventProto;
import ru.practicum.explorewithme.events.dto.*;
import ru.practicum.explorewithme.events.enumeration.EventRequestCountAction;
import ru.practicum.explorewithme.events.enumeration.EventState;
import ru.practicum.explorewithme.events.enumeration.EventStateAction;
import ru.practicum.explorewithme.events.mapper.EventMapper;
import ru.practicum.explorewithme.events.model.Event;
import ru.practicum.explorewithme.events.repository.EventRepository;
import ru.practicum.explorewithme.exception.ConflictException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.feign.category.CategoryFeign;
import ru.practicum.explorewithme.feign.dto.CategoryDto;
import ru.practicum.explorewithme.feign.dto.EventRequestCountDto;
import ru.practicum.explorewithme.feign.dto.ParticipationRequestDto;
import ru.practicum.explorewithme.feign.dto.UserDto;
import ru.practicum.explorewithme.feign.enumeration.RequestStatus;
import ru.practicum.explorewithme.feign.request.RequestFeign;
import ru.practicum.explorewithme.feign.user.UserFeign;
import ru.yandex.ewm.AnalyzerClient;
import ru.yandex.ewm.CollectorClient;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

import static ru.practicum.explorewithme.events.repository.EventRepository.AdminEventSpecification.withAdminEventParams;
import static ru.practicum.explorewithme.events.repository.EventRepository.UserEventSpecification.withUserEventParams;

@Service
@RequiredArgsConstructor
public class EventServiceImpl implements EventService {

    private final EventMapper eventMapper;
    private final EventRepository eventRepository;
    private final UserFeign userFeign;
    private final CategoryFeign categoryFeign;
    private final RequestFeign requestFeign;
    private final AnalyzerClient analyzerClient;
    private final CollectorClient collectorClient;

    @Override
    public EventDto addEvent(NewEventDto newEventDto, Long userId) {
        UserDto user = userFeign.findById(userId);
        CategoryDto category = categoryFeign.findById(newEventDto.getCategory());

        EventDto eventDto = eventMapper.convertShortDto(newEventDto);
        Event event = eventMapper.toModel(eventDto);
        event.setInitiatorId(user.getId());
        event.setCategoryId(category.getId());
        event.setCreatedOn(LocalDateTime.now());
        event.setParticipantCount(0);

        if (newEventDto.getPaid() == null) {
            event.setPaid(false);
        }

        if (newEventDto.getParticipantLimit() == null) {
            event.setParticipantLimit(0);
        }

        if (newEventDto.getRequestModeration() == null) {
            event.setRequestModeration(true);
        }

        Event savedEvent = eventRepository.save(event);
        EventDto savedEventDto = eventMapper.toDto(savedEvent);

        savedEventDto.setRating(0D);
        savedEventDto.setConfirmedRequests(0);

        return loadShortUserDto(savedEventDto);
    }

    @Override
    public EventDto updateEventByUser(Long eventId, NewEventDto newEventDto, Long userId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        if (!List.of(EventState.CANCELED, EventState.PENDING).contains(event.getState())) {
            throw new ConflictException("Only pending or canceled events can be changed");
        }

        return loadShortUserDto(doUpdateEvent(event, newEventDto));
    }

    @Override
    public EventDto updateEventByAdmin(Long eventId, NewEventDto newEventDto) {
        Event event = findEventById(eventId);

        if (!Objects.isNull(newEventDto.getStateAction())) {
            if (!List.of(EventStateAction.PUBLISH_EVENT, EventStateAction.REJECT_EVENT).contains(newEventDto.getStateAction())) {
                throw new ConflictException("Invalid action: " + newEventDto.getStateAction());
            }

            if (newEventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT) && !event.getState().equals(EventState.PENDING)) {
                throw new ConflictException("Cannot change state the event because it's not in the right state: PENDING");
            }

            if (newEventDto.getStateAction().equals(EventStateAction.REJECT_EVENT) && event.getState().equals(EventState.PUBLISHED)) {
                throw new ConflictException("Cannot change state the event because it's not in the right state: PUBLISHED");
            }

            if (newEventDto.getStateAction().equals(EventStateAction.PUBLISH_EVENT)) {
                LocalDateTime checkPublishDate = LocalDateTime.now().plusHours(1L);
                if (
                        (!Objects.isNull(newEventDto.getEventDate()) && checkPublishDate.isAfter(newEventDto.getEventDate()))
                                || checkPublishDate.isAfter(event.getEventDate())
                ) {
                    throw new ConflictException("The start date of the modified event must be no earlier than one hour from the publication date");
                }
            }
        }

        return loadShortUserDto(doUpdateEvent(event, newEventDto));
    }

    @Override
    public List<EventDto> findAllByParams(Long userId, Integer from, Integer size) {
        UserDto userDto = userFeign.findById(userId);
        int page = from / size;

        List<Event> events = eventRepository.findAllByInitiatorId(userDto.getId(), PageRequest.of(page, size)).stream().toList();

        List<EventDto> eventDtos = eventMapper.toDto(events);

        loadViews(eventDtos);
        loadConfirmedRequests(eventDtos);

        return loadShortUserDto(eventDtos);
    }

    @Override
    public EventDto findUserEvent(Long userId, Long eventId) {
        Event event = findEventById(eventId);
        checkInitiatorId(event, userId);

        EventDto eventDto = eventMapper.toDto(event);

        loadViews(List.of(eventDto));
        loadConfirmedRequests(List.of(eventDto));

        return loadShortUserDto(eventDto);
    }

    @Override
    public Event findEventById(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException("Event with id=" + eventId + " was not found"));
    }

    @Override
    public EventDto findEvent(Long eventId) {
        return loadShortUserDto(eventMapper.toDto(findEventById(eventId)));
    }

    @Override
    public List<EventDto> findAllByAdminParams(AdminEventParams adminEventParams) {
        PageRequest pageRequest = PageRequest.of(
                adminEventParams.getFrom() / adminEventParams.getSize(),
                adminEventParams.getSize(),
                Sort.by("eventDate").ascending()
        );

        Page<Event> events = eventRepository.findAll(withAdminEventParams(adminEventParams), pageRequest);

        List<EventDto> eventDtos = eventMapper.toDto(events.stream().toList());
        loadViews(eventDtos);
        loadConfirmedRequests(eventDtos);

        return loadShortUserDto(eventDtos);
    }

    @Override
    public List<EventDto> findAllByUserParams(UserEventParams userEventParams) {
        if (userEventParams.getRangeStart() != null && userEventParams.getRangeEnd() != null && !userEventParams.getRangeEnd().isAfter(userEventParams.getRangeStart())) {
            throw new ValidationException("rangeEnd must be after rangeStart");
        }

        PageRequest pageRequest = PageRequest.of(
                userEventParams.getFrom() / userEventParams.getSize(),
                userEventParams.getSize(),
                Sort.by("eventDate").ascending()
        );

        Page<Event> events = eventRepository.findAll(withUserEventParams(userEventParams), pageRequest);

        List<EventDto> eventDtos = eventMapper.toDto(events.stream().toList());
        loadViews(eventDtos);
        loadConfirmedRequests(eventDtos);

        if (userEventParams.getSort().isPresent() && userEventParams.getSort().get().equals(UserEventParams.EventSortEnum.VIEWS)) {
            eventDtos.sort(Comparator.comparing(EventDto::getRating).reversed());
        }

        return loadShortUserDto(eventDtos);
    }

    @Override
    public EventDto findPublishedEvent(Long eventId) {
        Event event = findEventById(eventId);

        if (!event.getState().equals(EventState.PUBLISHED)) {
            throw new NotFoundException("Event with id=" + eventId + " was not found");
        }

        EventDto eventDto = eventMapper.toDto(event);

        loadViews(List.of(eventDto));
        loadConfirmedRequests(List.of(eventDto));

        return loadShortUserDto(eventDto);
    }

    @Override
    public void incrementParticipantCnt(Long eventId, Integer increment, EventRequestCountAction action) {
        Integer coefficient = action.equals(EventRequestCountAction.MINUS) ? -1 : 1;
        eventRepository.incrementParticipantCount(eventId, increment * coefficient);
    }

    @Override
    public List<EventDto> getRecommendations(long userId, int maxResults) {
        List<Long> ids = analyzerClient.getRecommendations(userId, maxResults).stream()
                .sorted((a, b) -> (int) (a.getScore() - b.getScore()))
                .map(RecommendedEventProto::getEventId).toList();
        List<Event> events = eventRepository.findAllById(ids);
        List<EventDto> eventDtos = eventMapper.toDto(events);

        loadViews(eventDtos);
        loadConfirmedRequests(eventDtos);
        return loadShortUserDto(eventDtos);
    }

    @Override
    public void likeEvent(Long userId, Long eventId) {
        try {
            ParticipationRequestDto request = requestFeign.findByRequesterIdAndEventId(userId, eventId)
                    .orElseThrow(() -> new EntityNotFoundException("Request not found"));
            if (!request.getStatus().equals(RequestStatus.CONFIRMED)) {
                throw new ValidationException("The user can only like events they have attended.");
            }
            collectorClient.sendLike(userId, eventId);
        } catch (FeignException e) {
            throw new EntityNotFoundException("Error accessing request-service");
        }
    }

    private void checkInitiatorId(Event event, Long userId) {
        if (!event.getInitiatorId().equals(userId)) {
            throw new ConflictException("Event with id " + event.getId() + "don't compare with initiator " + userId);
        }
    }

    private EventDto loadShortUserDto(EventDto eventDto) {
        UserDto userDto = userFeign.findById(eventDto.getInitiator().getId());
        eventDto.getInitiator().setName(userDto.getName());
        return eventDto;
    }

    private List<EventDto> loadShortUserDto(List<EventDto> eventDtoList) {
        List<ShortUserDto> shortUserDtoList = new ArrayList<>();
        Set<Long> shortUserDtoIds = new HashSet<>();

        for (EventDto eventDto : eventDtoList) {
            shortUserDtoList.add(eventDto.getInitiator());
            shortUserDtoIds.add(eventDto.getInitiator().getId());
        }

        if (!shortUserDtoIds.isEmpty()) {
            List<UserDto> loadedUsers = userFeign.getUsers(shortUserDtoIds.stream().toList(), 0, shortUserDtoIds.size());

            HashMap<Long, UserDto> responseUsersMap = new HashMap<>();
            for (UserDto userDto : loadedUsers) {
                responseUsersMap.put(userDto.getId(), userDto);
            }

            for (ShortUserDto shortUserDto : shortUserDtoList) {
                shortUserDto.setName(responseUsersMap.get(shortUserDto.getId()).getName());
            }
        }

        return eventDtoList;
    }

    private void loadConfirmedRequests(List<EventDto> events) {
        List<Long> eventIds = events
                .stream()
                .map(EventDto::getId)
                .toList();

        List<EventRequestCountDto> eventsCountByStatus = requestFeign.findEventsCountByStatus(eventIds, RequestStatus.CONFIRMED);

        Map<Long, Long> requests = eventsCountByStatus
                .stream()
                .collect(Collectors.toMap(
                        EventRequestCountDto::getEventId,
                        EventRequestCountDto::getRequestsCount
                ));

        events.forEach(event -> {
            Long requestCount = requests.get(event.getId());
            requestCount = Objects.isNull(requestCount) ? 0L : requestCount;
            event.setConfirmedRequests(Math.toIntExact(requestCount));
        });
    }

    private void loadViews(List<EventDto> events) {
        Map<Long, Double> ratings = analyzerClient
                .getInteractionsCount(events.stream().map(EventDto::getId).collect(Collectors.toList()));

        events.forEach(event -> event.setRating(ratings.getOrDefault(event.getId(), 0.0)));
    }

    private EventDto doUpdateEvent(Event currentEvent, NewEventDto newEventDto) {
        if (!Objects.isNull(newEventDto.getAnnotation())) {
            currentEvent.setAnnotation(newEventDto.getAnnotation());
        }

        if (!Objects.isNull(newEventDto.getCategory())) {
            currentEvent.setCategoryId(categoryFeign.findById(newEventDto.getCategory()).getId());
        }

        if (!Objects.isNull(newEventDto.getDescription())) {
            currentEvent.setDescription(newEventDto.getDescription());
        }

        if (!Objects.isNull(newEventDto.getEventDate())) {
            currentEvent.setEventDate(newEventDto.getEventDate());
        }

        if (!Objects.isNull(newEventDto.getLocation())) {
            currentEvent.setLocationLat(newEventDto.getLocation().getLat());
            currentEvent.setLocationLon(newEventDto.getLocation().getLon());
        }

        if (!Objects.isNull(newEventDto.getPaid())) {
            currentEvent.setPaid(newEventDto.getPaid());
        }

        if (!Objects.isNull(newEventDto.getParticipantLimit())) {
            currentEvent.setParticipantLimit(newEventDto.getParticipantLimit());
        }

        if (!Objects.isNull(newEventDto.getRequestModeration())) {
            currentEvent.setRequestModeration(newEventDto.getRequestModeration());
        }

        if (!Objects.isNull(newEventDto.getTitle())) {
            currentEvent.setTitle(newEventDto.getTitle());
        }

        if (!Objects.isNull(newEventDto.getStateAction())) {
            switch (newEventDto.getStateAction()) {
                case REJECT_EVENT, CANCEL_REVIEW -> currentEvent.setState(EventState.CANCELED);
                case PUBLISH_EVENT -> {
                    currentEvent.setState(EventState.PUBLISHED);
                    currentEvent.setPublishedOn(LocalDateTime.now());
                }
                case SEND_TO_REVIEW -> currentEvent.setState(EventState.PENDING);
            }
        }

        eventRepository.save(currentEvent);

        EventDto eventDto = eventMapper.toDto(currentEvent);

        loadViews(List.of(eventDto));
        loadConfirmedRequests(List.of(eventDto));

        return eventDto;
    }
}
