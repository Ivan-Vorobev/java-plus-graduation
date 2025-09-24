package ru.practicum.ewm.configuration;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

@Data
@RequiredArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "analyzer.kafka")
public class KafkaConfig {
    private ConsumerConfig consumer;
    private Map<String, String> topics;

    @Data
    public static class ConsumerConfig {
        private KafkaProperties userAction;
        private KafkaProperties eventSimilarity;
    }

    @Data
    public static class KafkaProperties {
        private Properties properties;
    }

    public Properties getUserActionProperties() {
        return this.consumer.userAction.getProperties();
    }

    public Properties getEventsSimilarityProperties() {
        return this.consumer.eventSimilarity.getProperties();
    }

    @PostConstruct
    public void init() {
        TopicType.setTopics(topics);
    }

    public enum TopicType {
        USER_ACTIONS("user-actions"),
        EVENTS_SIMILARITY("events-similarity");

        private final String topicKey;

        private static Map<String, String> topics = HashMap.newHashMap(0);

        TopicType(String topicKey) {
            this.topicKey = topicKey;
        }

        public static void setTopics(Map<String, String> topics) {
            TopicType.topics = topics;
        }

        public String getTopic() {
            String topic = topics.get(topicKey);

            if (topic == null) {
                throw new IllegalArgumentException("Incorrect config key value '" + topicKey + "' for kafka topics");
            }

            return topic;
        }
    }
}