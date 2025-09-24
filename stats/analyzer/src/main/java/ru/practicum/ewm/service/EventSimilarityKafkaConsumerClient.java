package ru.practicum.ewm.service;

import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.util.List;
import java.util.Properties;

@Service
public class EventSimilarityKafkaConsumerClient extends AbstractKafkaConsumerClient<EventSimilarityAvro> {
    public EventSimilarityKafkaConsumerClient(KafkaConfig kafkaConfig) {
        super(kafkaConfig);
    }

    @Override
    protected Properties getConfig(KafkaConfig kafkaConfig) {
        return kafkaConfig.getEventsSimilarityProperties();
    }

    @Override
    protected List<String> getTopics(KafkaConfig kafkaConfig) {
        return List.of(KafkaConfig.TopicType.EVENTS_SIMILARITY.getTopic());
    }
}
