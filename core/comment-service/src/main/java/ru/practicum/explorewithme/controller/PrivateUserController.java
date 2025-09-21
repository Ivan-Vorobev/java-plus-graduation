package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.CommentService;
import ru.practicum.explorewithme.dto.CommentDto;
import ru.practicum.explorewithme.dto.NewCommentDto;

import java.util.List;

@Slf4j
@Validated
@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
public class PrivateUserController {

    private final CommentService commentService;

    @PostMapping("/{userId}/events/{eventId}/comments")
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @RequestBody NewCommentDto newCommentDto
    ) {
        return commentService.createComment(userId, eventId, newCommentDto);
    }

    @GetMapping("/{userId}/comments")
    public List<CommentDto> findApprovedCommentsOnUser(@PathVariable Long userId) {
        return commentService.findApprovedCommentsOnUserId(userId);
    }
}