package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.mapper.UserActionMapper;
import ru.practicum.ewm.model.UserAction;
import ru.practicum.ewm.repository.UserActionRepository;
import ru.practicum.ewm.stats.avro.UserActionAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class UserActionHandler {

    private final UserActionRepository actionRepository;
    private final UserActionMapper actionMapper;

    public void handleUserAction(UserActionAvro avro) {
        UserAction action = actionMapper.mapToUserAction(avro);

        if (!actionRepository.existsByEventIdAndUserId(action.getEventId(), action.getUserId())) {
            action = actionRepository.save(action);
            log.info("Saving new action: {}", action);
        } else {
            UserAction oldAction = actionRepository
                    .findByEventIdAndUserId(action.getEventId(), action.getUserId()).get();
            log.info("Finding the old action in the database: {}", oldAction);
            if (action.getWeight() > oldAction.getWeight()) {
                oldAction.setWeight(action.getWeight());
                oldAction.setTimestamp(action.getTimestamp());
                oldAction = actionRepository.save(oldAction);
                log.info("The action weight has increased, updating in the database: {}", oldAction);
            } else {
                log.info("The action weight has not increased, no need to update");
            }
        }
    }
}
