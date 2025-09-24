package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;

@Slf4j
@Service
public class KafkaConsumerClient implements AutoCloseable {
    private final KafkaConsumer<String, SpecificRecordBase> consumer;

    public KafkaConsumerClient(KafkaConfig kafkaConfig) {
        consumer = new KafkaConsumer<>(kafkaConfig.getConsumerProperties());
    }

    public KafkaConsumer<String, SpecificRecordBase> getInstance() {
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
