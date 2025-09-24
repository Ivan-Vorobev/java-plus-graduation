package ru.practicum.explorewithme.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.explorewithme.model.EventRequestCount;
import ru.practicum.explorewithme.model.ParticipationRequest;
import ru.practicum.explorewithme.model.RequestStatus;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<ParticipationRequest, Long> {
    Optional<ParticipationRequest> findByRequesterIdAndEventId(Long userId, Long eventId);

    List<ParticipationRequest> findParticipationRequestByRequesterId(long userId);

    long countParticipationRequestByRequesterIdAndEventId(long userId, long eventId);

    @Query("SELECT NEW ru.practicum.explorewithme.model.EventRequestCount(r.eventId, count(r.id)) FROM ParticipationRequest r WHERE r.status = :status AND r.eventId in :eventIds GROUP BY r.eventId ORDER BY count(r.id) ASC")
    List<EventRequestCount> findEventsCountByStatus(@Param("eventIds") List<Long> eventIds, @Param("status") RequestStatus status);

    Optional<ParticipationRequest> findParticipationRequestByIdAndRequesterId(long requestId, long userId);

    List<ParticipationRequest> findParticipationRequestByEventId(long eventId);

    long countParticipationRequestByEventIdAndStatus(long eventId, RequestStatus status);
}
