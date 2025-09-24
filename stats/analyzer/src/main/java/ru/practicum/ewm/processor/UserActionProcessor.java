package ru.practicum.ewm.processor;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.errors.WakeupException;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.handler.UserActionHandler;
import ru.practicum.ewm.service.UserActionKafkaConsumerClient;
import ru.practicum.ewm.stats.avro.UserActionAvro;

import java.time.Duration;

@Slf4j
@Component
public class UserActionProcessor {

    private final UserActionKafkaConsumerClient consumerClient;
    private final UserActionHandler userActionHandler;
    private static final Duration CONSUME_POLL_TIMEOUT = Duration.ofMillis(1000);

    public UserActionProcessor(UserActionKafkaConsumerClient consumerClient, UserActionHandler userActionHandlers) {
        this.consumerClient = consumerClient;
        this.userActionHandler = userActionHandlers;
        Runtime.getRuntime().addShutdownHook(new Thread(() -> consumerClient.getInstance().wakeup()));
    }

    public void start() {
        try {
            while (true) {
                ConsumerRecords<Long, UserActionAvro> records = consumerClient.getInstance().poll(CONSUME_POLL_TIMEOUT);
                if (!records.isEmpty()) {
                    for (ConsumerRecord<Long, UserActionAvro> record : records) {
                        UserActionAvro event = record.value();
                        log.info("{}: Payload from kafka: {}", this.getClass().getSimpleName(), event);
                        userActionHandler.handleUserAction(event);
                    }
                    consumerClient.getInstance().commitSync();
                }
            }
        } catch (WakeupException ignored) {

        } catch (Exception e) {
            log.error("{}: Error handle process", this.getClass().getSimpleName(), e);
        } finally {
            consumerClient.getInstance().close();
        }
    }
}
