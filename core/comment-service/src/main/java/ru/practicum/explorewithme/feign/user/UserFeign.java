package ru.practicum.explorewithme.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.dto.UserDto;

@FeignClient(name = "user-service")
public interface UserFeign {
    @GetMapping("/internal/users/{userId}")
    UserDto findById(@PathVariable(name = "userId") Long userId);
}
