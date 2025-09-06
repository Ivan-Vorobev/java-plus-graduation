import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ContextConfiguration;
import ru.yandex.practicum.client.hit.HitClient;
import ru.yandex.practicum.client.stats.StatsClient;
import ru.yandex.practicum.dto.CreateHitDTO;
import ru.yandex.practicum.dto.HitsStatDTO;
import ru.yandex.practicum.ewm.StatsServer;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.Optional;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ContextConfiguration(classes = StatsServer.class)
@Import({StatsClient.class, HitClient.class})
public class StatsClientTest {
    @LocalServerPort
    private int port;
    @Autowired
    private StatsClient statsClient;
    @Autowired
    private HitClient hitClient;


    @Test
    public void shouldCreateHitsAndReturnHitsStatistic() {
        String serverUrl = "http://localhost:" + port;
        statsClient.setServerUrl(serverUrl);
        hitClient.setServerUrl(serverUrl);

        hitClient.hit(CreateHitDTO.builder().app("test1").ip("127.0.0.2").uri("/events").timestamp(LocalDateTime.now()).build());
        hitClient.hit(CreateHitDTO.builder().app("test2").ip("127.0.0.3").uri("/events").timestamp(LocalDateTime.now()).build());
        hitClient.hit(CreateHitDTO.builder().app("test2").ip("127.0.0.4").uri("/events").timestamp(LocalDateTime.now()).build());

        Optional<Collection<HitsStatDTO>> result = statsClient.getAll(
                LocalDateTime.now().minusDays(5L),
                LocalDateTime.now().plusDays(5L),
                List.of("/events"),
                null);

        Assertions.assertNotNull(result);
        Assertions.assertTrue(result.isPresent());
        Assertions.assertEquals(result.get().size(), 2);
    }
}
