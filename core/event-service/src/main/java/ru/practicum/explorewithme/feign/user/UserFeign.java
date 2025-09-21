package ru.practicum.explorewithme.feign.user;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.explorewithme.feign.dto.UserDto;

import java.util.List;

@FeignClient(name = "user-service")
public interface UserFeign {
    @GetMapping("/internal/users/{userId}")
    UserDto findById(@PathVariable(name = "userId") Long userId);

    @GetMapping("/admin/users")
    List<UserDto> getUsers(@RequestParam(required = false) List<Long> ids,
                                  @RequestParam(defaultValue = "0") int from,
                                  @RequestParam(defaultValue = "10") int size);
}
