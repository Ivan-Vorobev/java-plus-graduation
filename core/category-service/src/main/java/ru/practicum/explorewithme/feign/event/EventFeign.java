package ru.practicum.explorewithme.feign.event;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.feign.dto.EventDto;

import java.util.List;

@FeignClient(name = "event-service")
public interface EventFeign {
    @GetMapping("/admin/events")
    List<EventDto> getEventsByCategories(
            @RequestParam(required = false) List<Long> categories
    );
}
