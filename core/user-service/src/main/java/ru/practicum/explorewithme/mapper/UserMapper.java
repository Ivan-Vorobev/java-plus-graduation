package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import ru.practicum.explorewithme.dto.NewUserRequest;
import ru.practicum.explorewithme.dto.UserDto;
import ru.practicum.explorewithme.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDto toUserDto(User user);

    User toUser(NewUserRequest newUserRequest);
}