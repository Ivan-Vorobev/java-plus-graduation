package ru.practicum.ewm;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.processor.EventSimilarityProcessor;
import ru.practicum.ewm.processor.UserActionProcessor;

@Slf4j
@Component
@RequiredArgsConstructor
public class AnalyzerStarter implements CommandLineRunner {
    private final EventSimilarityProcessor eventSimilarityProcessor;
    private final UserActionProcessor userActionProcessor;

    @Override
    public void run(String... args) throws Exception {
        Thread hubEventsThread = new Thread(eventSimilarityProcessor);
        hubEventsThread.setName("EventSimilarityHandlerThread");
        hubEventsThread.start();

        userActionProcessor.start();
    }
}
