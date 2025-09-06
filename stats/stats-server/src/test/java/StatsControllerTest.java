import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import ru.yandex.practicum.dto.HitsStatDTO;
import ru.yandex.practicum.ewm.StatsServer;
import ru.yandex.practicum.ewm.stats.controller.StatsController;
import ru.yandex.practicum.ewm.stats.service.StatsService;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(StatsController.class)
@ContextConfiguration(classes = StatsServer.class)
class StatsControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private StatsService statsService;

    @Test
    void testValidRequestReturnsStats() throws Exception {
        List<HitsStatDTO> stats = List.of(
                HitsStatDTO.builder().app("ewm-main-service").uri("/events/1").hits(5L).build(),
                HitsStatDTO.builder().app("ewm-main-service").uri("/events/2").hits(10L).build()
        );

        LocalDateTime start = LocalDateTime.of(2024, 1, 1, 0, 0);
        LocalDateTime end = LocalDateTime.of(2024, 12, 31, 23, 59);

        when(statsService.getStats(start, end, null, false)).thenReturn(stats);

        mockMvc.perform(get("/stats")
                        .param("start", "2024-01-01 00:00:00")
                        .param("end", "2024-12-31 23:59:00"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].app").value("ewm-main-service"))
                .andExpect(jsonPath("$[0].uri").value("/events/1"))
                .andExpect(jsonPath("$[0].hits").value(5))
                .andExpect(jsonPath("$[1].uri").value("/events/2"));
    }

    @Test
    void testMissingStartParamReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("end", "2024-12-31 23:59:00"))
                .andExpect(status().is4xxClientError());
    }

    @Test
    void testInvalidDateFormatReturnsBadRequest() throws Exception {
        mockMvc.perform(get("/stats")
                        .param("start", "invalid-date")
                        .param("end", "2024-12-31 23:59:00"))
                .andExpect(status().is5xxServerError());
    }
}