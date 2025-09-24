package ru.practicum.ewm.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.springframework.stereotype.Service;
import ru.practicum.ewm.configuration.KafkaConfig;

import java.time.Duration;
import java.time.Instant;

@Slf4j
@Service
public class KafkaProducerClient implements AutoCloseable {
    private final KafkaProducer<Long, SpecificRecordBase> producer;

    public KafkaProducerClient(KafkaConfig kafkaConfig) {
        producer = new KafkaProducer<>(kafkaConfig.getProducerProperties());
    }

    public void send(SpecificRecordBase event, Long hubId, Instant timestamp, KafkaConfig.TopicType topicType) {

        ProducerRecord<Long, SpecificRecordBase> record = new ProducerRecord<>(
                topicType.getTopic(),
                null,
                timestamp.toEpochMilli(),
                hubId,
                event
        );

        log.trace("Send data: {}, hub: {}, topic: {}", event, hubId, topicType.getTopic());

        producer.send(record, (metadata, exception) -> {
            if (exception == null) {
                log.info("Message sent successfully to partition " + metadata.partition() + " with offset " + metadata.offset());
            } else {
                log.error("Failed to send message: " + exception.getMessage());
            }
        });
    }

    @Override
    public void close() {
        producer.flush();
        producer.close(Duration.ofSeconds(10));
    }
}
