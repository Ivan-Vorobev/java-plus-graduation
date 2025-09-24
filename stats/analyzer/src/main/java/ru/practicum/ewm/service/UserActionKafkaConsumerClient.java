package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.util.List;
import java.util.Properties;

@Service
public class UserActionKafkaConsumerClient extends AbstractKafkaConsumerClient<UserActionAvro> {
    public UserActionKafkaConsumerClient(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

    @Override
    protected Properties getConfig(KafkaConfig kafkaConfig) {
        return kafkaConfig.getUserActionProperties();
    }

    @Override
    protected List<String> getTopics(KafkaConfig kafkaConfig) {
        return List.of(KafkaConfig.TopicType.USER_ACTIONS.getTopic());
    }
}
