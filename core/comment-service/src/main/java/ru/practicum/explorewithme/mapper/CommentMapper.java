package ru.practicum.explorewithme.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.model.Comment;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    @Mapping(target = "eventId", source = "comment.eventId")
    @Mapping(target = "authorId", source = "comment.authorId")
    CommentDto toDto(Comment comment);

    @Mapping(target = "eventId", source = "comment.eventId")
    @Mapping(target = "authorId", source = "comment.authorId")
    List<CommentDto> toDto(List<Comment> comments);
}