package ru.practicum.ewm.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.handler.EventSimilarityHandler;
import ru.practicum.ewm.service.EventSimilarityKafkaConsumerClient;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;

@Slf4j
@Component
public class EventSimilarityProcessor implements Runnable {
    private final EventSimilarityKafkaConsumerClient consumerClient;
    private final EventSimilarityHandler eventSimilarityHandler;

    private static final Duration CONSUME_POLL_TIMEOUT = Duration.ofMillis(1000);

    public EventSimilarityProcessor(EventSimilarityKafkaConsumerClient consumerClient, EventSimilarityHandler sensorsSnapshotHandler) {
        this.consumerClient = consumerClient;
        this.eventSimilarityHandler = sensorsSnapshotHandler;

        Runtime.getRuntime().addShutdownHook(new Thread(() -> consumerClient.getInstance().wakeup()));
    }

    @Override
    public void run() {
        try {
            while (true) {
                ConsumerRecords<Long, EventSimilarityAvro> records = consumerClient.getInstance().poll(CONSUME_POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, EventSimilarityAvro> record : records) {

                        log.info("{}: Payload from kafka: {}", this.getClass().getSimpleName(), record);
                        eventSimilarityHandler.handleEventSimilarity(record.value());

                    }
                    consumerClient.getInstance().commitSync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: Error handle process", this.getClass().getSimpleName(), e);
        } finally {
            consumerClient.close();
        }
    }
}

