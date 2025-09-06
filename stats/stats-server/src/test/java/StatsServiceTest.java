import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.yandex.practicum.dto.HitsStatDTO;
import ru.yandex.practicum.ewm.stats.repository.HitsRepository;
import ru.yandex.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class StatsServiceTest {

    private HitsRepository hitsRepository;
    private StatsService statsService;

    @BeforeEach
    void setUp() {
        hitsRepository = mock(HitsRepository.class);
        statsService = new StatsService(hitsRepository);
    }

    @Test
    void testGetStatsWithUniqueFalseAndNoUris() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        List<HitsStatDTO> repoResult = List.of(
                new HitsStatDTO("ewm-main-service", "/events/1", 5L),
                new HitsStatDTO("ewm-main-service", "/events/2", 10L)
        );

        when(hitsRepository.findAllStats(start, end)).thenReturn(repoResult);

        List<HitsStatDTO> result = statsService.getStats(start, end, null, false);

        assertEquals(2, result.size());
        assertEquals("/events/1", result.get(0).getUri());
        assertEquals(5L, result.get(0).getHits());
        assertEquals("/events/2", result.get(1).getUri());
        assertEquals(10L, result.get(1).getHits());
    }

    @Test
    void testGetStatsWithUniqueTrueAndUris() {
        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);
        List<String> uris = List.of("/events/1");

        List<HitsStatDTO> repoResult = List.of(
                new HitsStatDTO("ewm-main-service", "/events/1", 3L)
        );

        when(hitsRepository.findUniqueIpStatsForUris(start, end, uris)).thenReturn(repoResult);

        List<HitsStatDTO> result = statsService.getStats(start, end, uris, true);

        assertEquals(1, result.size());
        assertEquals("/events/1", result.getFirst().getUri());
        assertEquals(3L, result.getFirst().getHits());
    }
}