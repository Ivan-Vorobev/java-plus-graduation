package ru.practicum.explorewithme.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.yandex.ewm.AnalyzerClient;
import ru.yandex.ewm.CollectorClient;

@Configuration
public class StatsClientConfig {
    @Bean
    public CollectorClient collectorClient() {
        return new CollectorClient();
    }

    @Bean
    public AnalyzerClient analyzerClient() {
        return new AnalyzerClient();
    }
}
