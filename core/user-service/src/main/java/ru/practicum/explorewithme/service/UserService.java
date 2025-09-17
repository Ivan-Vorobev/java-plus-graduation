package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.model.User;

import java.util.List;

public interface UserService {
    UserDto createUser(NewUserRequest request);

    List<UserDto> getUsers(List<Long> ids, int from, int size);

    void deleteUser(Long userId);

    UserDto getById(Long userId);

    User getUser(Long userId);
}