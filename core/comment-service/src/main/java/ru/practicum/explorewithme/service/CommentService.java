package ru.practicum.explorewithme.service;

import ru.practicum.explorewithme.dto.AdminCommentParams;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;
import ru.practicum.explorewithme.model.CommentStatus;

import java.util.List;

public interface CommentService {

    List<CommentDto> findComments(long eventId);

    CommentDto findComment(long eventId, long commentId);

    CommentDto findCommentById(long commentId);

    CommentDto createComment(long userId, long eventId, NewCommentDto newCommentDto);

    void deleteComment(long commentId);

    CommentDto patchCommentStatus(long commentId, CommentStatus status);

    List<CommentDto> findApprovedCommentsOnUserId(long userId);

    List<CommentDto> findAllByAdminParams(AdminCommentParams params);
}
