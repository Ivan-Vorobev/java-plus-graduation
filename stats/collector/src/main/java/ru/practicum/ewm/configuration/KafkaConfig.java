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
@ConfigurationProperties(prefix = "collector.kafka")
public class KafkaConfig {
    private ProducerConfig producer;
    private Map<String, String> topics;

    public Properties getProducerProperties() {
        return this.producer.getProperties();
    }

    @PostConstruct
    public void init() {
        TopicType.setTopics(topics);
    }

    @Data
    public static class ProducerConfig {
        private Properties properties;
    }

    public enum TopicType {
        USER_ACTIONS("user-actions");

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