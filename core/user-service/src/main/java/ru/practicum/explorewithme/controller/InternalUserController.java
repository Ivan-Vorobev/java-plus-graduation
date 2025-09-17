package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.service.UserService;


@RestController
@RequestMapping("/internal/users")
@RequiredArgsConstructor
public class InternalUserController {

    private final UserService userService;

    @GetMapping("/{userId}")
    @ResponseStatus(HttpStatus.OK)
    public UserDto deleteUser(@PathVariable Long userId) {
        return userService.getById(userId);
    }
}