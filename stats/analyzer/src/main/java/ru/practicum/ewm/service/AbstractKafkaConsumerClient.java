package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;

import java.util.List;
import java.util.Properties;

@Slf4j
@Service
public abstract class AbstractKafkaConsumerClient<T extends SpecificRecordBase> implements AutoCloseable {
    private final KafkaConsumer<Long, T> consumer;

    public AbstractKafkaConsumerClient(KafkaConfig kafkaConfig) {
        consumer = new KafkaConsumer<>(getConfig(kafkaConfig));
        consumer.subscribe(getTopics(kafkaConfig));
    }

    abstract protected Properties getConfig(KafkaConfig kafkaConfig);

    abstract protected List<String> getTopics(KafkaConfig kafkaConfig);

    public KafkaConsumer<Long, T> getInstance() {
        return consumer;
    }

    @Override
    public void close() {
        try {
            consumer.commitSync();
        } finally {
            consumer.close();
        }

    }
}
