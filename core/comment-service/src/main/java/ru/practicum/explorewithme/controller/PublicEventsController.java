package ru.practicum.explorewithme.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.service.CommentService;
import ru.practicum.explorewithme.dto.CommentDto;

import java.util.List;

@RestController
@RequestMapping(path = "/events")
@RequiredArgsConstructor
public class PublicEventsController {

    private final CommentService commentService;

    @GetMapping("/{eventId}/comments")
    public List<CommentDto> getComments(@PathVariable Long eventId) {
        return commentService.findComments(eventId);
    }

    @GetMapping("/{eventId}/comments/{commentId}")
    public CommentDto getComment(@PathVariable Long eventId, @PathVariable Long commentId) {
        return commentService.findComment(eventId, commentId);
    }
}
