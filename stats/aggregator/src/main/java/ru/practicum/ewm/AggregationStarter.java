package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.configuration.KafkaConfig;
import ru.practicum.ewm.service.AggregatorService;
import ru.practicum.ewm.service.KafkaConsumerClient;
import ru.practicum.ewm.service.KafkaProducerClient;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class AggregationStarter {

    private final KafkaProducerClient producerClient;
    private final KafkaConsumerClient consumerClient;
    private final AggregatorService aggregatorService;

    private static final Duration CONSUME_POLL_TIMEOUT = Duration.ofMillis(1000);

    public void start() {
        KafkaConsumer<String, SpecificRecordBase> consumer = consumerClient.getInstance();
        Runtime.getRuntime().addShutdownHook(new Thread(consumer::wakeup));
        try {
            consumer.subscribe(List.of(KafkaConfig.TopicType.USER_ACTIONS.getTopic()));
            while (true) {
                ConsumerRecords<String, SpecificRecordBase> records = consumer.poll(CONSUME_POLL_TIMEOUT);
                for (ConsumerRecord<String, SpecificRecordBase> record : records) {
                    List<EventSimilarityAvro> eventsSimilarity = aggregatorService.aggregationUserAction(record.value());
                    if (!eventsSimilarity.isEmpty()) {
                        sendSimilarity(eventsSimilarity);
                    }
                }
                consumer.commitAsync();
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("Error while processing events from sensors", e);
        } finally {
            log.info("Start close Consumer");
            consumerClient.close();
            log.info("Start close Producer");
            producerClient.close();
        }
    }

    private void sendSimilarity(List<EventSimilarityAvro> eventsSimilarity) {
        for (EventSimilarityAvro sim : eventsSimilarity) {
            log.info("Passing a message via kafka-producer: {}", sim);
            producerClient.send(
                    sim,
                    sim.getEventA(),
                    sim.getTimestamp(),
                    KafkaConfig.TopicType.EVENTS_SIMILARITY
            );
        }
    }
}
