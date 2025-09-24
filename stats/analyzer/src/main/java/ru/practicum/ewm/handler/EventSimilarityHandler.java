package ru.practicum.ewm.handler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import ru.practicum.ewm.mapper.EventSimilarityMapper;
import ru.practicum.ewm.model.EventSimilarity;
import ru.practicum.ewm.repository.EventSimilarityRepository;
import ru.practicum.ewm.stats.avro.EventSimilarityAvro;

@Slf4j
@Component
@RequiredArgsConstructor
public class EventSimilarityHandler {
    private final EventSimilarityRepository similarityRepository;
    private final EventSimilarityMapper similarityMapper;

    public void handleEventSimilarity(EventSimilarityAvro avro) {
        EventSimilarity similarity = similarityMapper.mapToEventSimilarity(avro);
        if (!similarityRepository.existsByEventAAndEventB(similarity.getEventA(), similarity.getEventB())) {
            similarity = similarityRepository.save(similarity);
            log.info("Saving new similarity: {}", similarity);
        } else {
            EventSimilarity oldSimilarity = similarityRepository
                    .findByEventAAndEventB(similarity.getEventA(), similarity.getEventB()).get();
            log.info("We find an old similarity in the database: {}", oldSimilarity);
            if (similarity.getScore() > oldSimilarity.getScore()) {
                oldSimilarity.setScore(similarity.getScore());
                oldSimilarity.setTimestamp(similarity.getTimestamp());
                oldSimilarity = similarityRepository.save(oldSimilarity);
                log.info("Similarity increased, updating in the database: {}", oldSimilarity);
            } else {
                log.info("The similarity has not increased, so there is no need to update.");
            }
        }
    }
}
