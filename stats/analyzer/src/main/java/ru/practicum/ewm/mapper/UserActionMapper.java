package ru.practicum.ewm.mapper;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.stats.avro.ActionTypeAvro;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
public class UserActionMapper {
    public UserAction mapToUserAction(UserActionAvro avro) {
        return UserAction.builder()
                .eventId(avro.getEventId())
                .userId(avro.getUserId())
                .weight(getWeight(avro.getActionType()))
                .timestamp(avro.getTimestamp())
                .build();
    }

    private Double getWeight(ActionTypeAvro type) {
        return switch (type) {
            case VIEW -> 0.4;
            case REGISTER -> 0.8;
            case LIKE -> 1.0;
        };
    }
}