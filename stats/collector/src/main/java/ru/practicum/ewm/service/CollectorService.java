package ru.practicum.ewm.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;
import ru.practicum.ewm.mapper.UserActionMapper;
import ru.practicum.ewm.stats.avro.UserActionAvro;
import ru.practicum.ewm.stats.messages.UserActionProto;

@Slf4j
@Service
@RequiredArgsConstructor
public class CollectorService {
    private final KafkaProducerClient producer;
    private final UserActionMapper userActionMapper;

    public void collectUserAction(UserActionProto request) {
        UserActionAvro actionAvro = userActionMapper.mapToAvro(request);
        log.info("Sending to kafka-topic: {}, user action: {}", KafkaConfig.TopicType.USER_ACTIONS, actionAvro);
        producer.send(
                actionAvro,
                actionAvro.getEventId(),
                actionAvro.getTimestamp(),
                KafkaConfig.TopicType.USER_ACTIONS
        );
    }
}
