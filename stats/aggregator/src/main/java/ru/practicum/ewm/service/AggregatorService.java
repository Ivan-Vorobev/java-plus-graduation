package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.*;


@Slf4j
@Component
@RequiredArgsConstructor
public class AggregatorService {
    private final Map<Long, Map<Long, Double>> eventUserWeight = new HashMap<>();
    private final Map<Long, Double> eventWeightSum = new HashMap<>();
    private final Map<Long, Map<Long, Double>> twoEventsMinSum = new HashMap<>();

    public List<EventSimilarityAvro> aggregationUserAction(SpecificRecordBase userActionAvro) {
        UserActionAvro action = (UserActionAvro) userActionAvro;
        List<EventSimilarityAvro> result = new ArrayList<>();
        Double weight = getWeight(action.getActionType());
        Double currentEventUserWeight = eventUserWeight
                .computeIfAbsent(action.getEventId(), e -> new HashMap<>())
                .getOrDefault(action.getUserId(), 0.0);
        Double weightDiff = weight - currentEventUserWeight;

        if (currentEventUserWeight >= weight) {
            log.info("The weight of user action {} has not changed. Event id: {}, action: {}", action.getUserId(), action.getEventId(), action.getActionType());
            return List.of();
        }

        updateEventUserWeight(action, weight);
        updateEventWeightSum(action, weight, weightDiff);

        List<Long> eventIdsForCalculate = getEventIdsForCalculate(action);

        if (eventIdsForCalculate.isEmpty()) {
            log.info("No events found for similarity calculation, returning an empty list");
            return List.of();
        }

        log.info("Events to calculate similarity: {}", eventIdsForCalculate);

        updateTwoEventsMinSum(action, weight, weightDiff, eventIdsForCalculate);

        log.info("Calculating similarity for event {} with events: {}", action.getEventId(), eventIdsForCalculate);
        for (Long id : eventIdsForCalculate) {
            Long first = Math.min(action.getEventId(), id);
            Long second = Math.max(action.getEventId(), id);

            double similarity = twoEventsMinSum.get(first).get(second) /
                    (Math.sqrt(eventWeightSum.get(first)) * Math.sqrt(eventWeightSum.get(second)));
            log.info("The similarity of event {} and {} is: {}", first, second, similarity);
            EventSimilarityAvro eventSimilarity = EventSimilarityAvro.newBuilder()
                    .setEventA(first)
                    .setEventB(second)
                    .setScore(similarity)
                    .setTimestamp(action.getTimestamp())
                    .build();
            result.add(eventSimilarity);
        }

        log.info("Calculation result: {}", result);
        return result;
    }

    private void updateEventWeightSum(UserActionAvro action, Double weight, Double weightDiff) {
        if (!eventWeightSum.containsKey(action.getEventId())) {
            eventWeightSum.put(action.getEventId(), weight);
            log.info("The event is new, the sum will be equal to eventWeight: {}", weight);
        } else {
            Double newSum = eventWeightSum.merge(action.getEventId(), weightDiff, Double::sum);
            log.info("The new sum in eventWeightSum for event {} is: {}", action.getEventId(), newSum);
        }
    }

    private void updateTwoEventsMinSum(UserActionAvro action, Double eventWeight, Double diffWeight, List<Long> eventIdsForCalculate) {
        Long userId = action.getUserId();
        long eventId = action.getEventId();

        Double oldEventWeight = eventWeight - diffWeight;

        for (Long otherEventId : eventIdsForCalculate) {
            Double otherEventWeight = eventUserWeight.get(otherEventId).get(userId);

            Long first = Math.min(eventId, otherEventId);
            Long second = Math.max(eventId, otherEventId);

            Double oldSum = twoEventsMinSum.computeIfAbsent(first, k -> new HashMap<>()).get(second);
            log.info("Old minimum amount {}, for events {} and {}", oldSum, eventId, otherEventId);
            if (oldSum == null) {
                twoEventsMinSum.get(first).put(second, Math.min(eventWeight, otherEventWeight));
                log.info("The minimum amount is not yet calculated, save a new one: first - {}, second - {}, sum - {}",
                        first, second, Math.min(eventWeight, otherEventWeight));
                continue;
            }

            if (eventWeight >= otherEventWeight) {
                log.info("eventWeight {} >= otherEventWeight {}", eventWeight, otherEventWeight);
                if (oldEventWeight >= otherEventWeight) {
                    log.info("oldEventWeight {} >= otherEventWeight {}, no need to update the sum", oldEventWeight, otherEventWeight);
                    continue;
                } else {
                    log.info("oldEventWeight {} < otherEventWeight {}", oldEventWeight, otherEventWeight);
                    oldSum += otherEventWeight - oldEventWeight;
                    log.info("New minimum amount: {}", oldSum);
                }
            } else {
                log.info("eventWeight {} < otherEventWeight {}", eventWeight, otherEventWeight);
                oldSum += diffWeight;
                log.info("New minimum amount: {}", oldSum);
            }
            twoEventsMinSum.get(first).put(second, oldSum);
            log.info("Updating the minimum amount: first - {}, second - {}, sum - {}", first, second, oldSum);
        }
    }

    private List<Long> getEventIdsForCalculate(UserActionAvro action) {
        List<Long> eventIdsForCalculate = new ArrayList<>();
        for (Long id : eventUserWeight.keySet()) {
            if (!id.equals(action.getEventId())) {
                Double otherEventUserWeight = eventUserWeight.get(id).get(action.getUserId());
                if (otherEventUserWeight != null && otherEventUserWeight != 0) {
                    eventIdsForCalculate.add(id);
                }
            }
        }

        return eventIdsForCalculate;
    }

    private void updateEventUserWeight(UserActionAvro action, Double weight) {
        log.info("Update weight of user action {}. Event id: {}, action: {}, weight: {}", action.getUserId(), action.getEventId(), action.getActionType(), weight);
        eventUserWeight.get(action.getEventId()).put(action.getUserId(), weight);
    }

    private Double getWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}
