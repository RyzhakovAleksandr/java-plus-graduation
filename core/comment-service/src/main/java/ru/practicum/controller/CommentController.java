package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.constant.Message;
import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.service.CommentService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController {
    private final CommentService commentService;

    private static final String USER_COMMENTS_EVENT = "/user/{userId}/comments/{eventId}";
    private static final String USER_COMMENTS_EVENT_COMMENT = "/user/{userId}/comments/{eventId}/{commentId}";
    private static final String USER_COMMENTS = "/user/{userId}/comments";
    private static final String USER_COMMENTS_ID = "/user/{userId}/comments/{commentId}";
    private static final String ADMIN_COMMENTS_ID = "/admin/comments/{commentId}";
    private static final String COMMENTS_EVENT = "/comments/event/{eventId}";
    private static final String COMMENTS_ID = "/comments/{commentId}";

    @PostMapping(USER_COMMENTS_EVENT)
    public ResponseEntity<CommentDto> addComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info(Message.ADD_COMMENT, userId, eventId);
        CommentDto result = commentService.addComment(userId, eventId, newCommentDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(result);
    }

    @PatchMapping(USER_COMMENTS_EVENT_COMMENT)
    public ResponseEntity<CommentDto> updateComment(
            @PathVariable Long userId,
            @PathVariable Long eventId,
            @PathVariable Long commentId,
            @Valid @RequestBody NewCommentDto newCommentDto) {
        log.info(Message.UPDATE_COMMENT, userId, eventId, commentId);
        CommentDto result = commentService.updateComment(userId, eventId, commentId, newCommentDto);
        return ResponseEntity.ok(result);
    }

    @GetMapping(USER_COMMENTS)
    public ResponseEntity<List<CommentDto>> getCommentsByAuthor(
            @PathVariable Long userId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.GET_COMMENTS_BY_AUTHOR, userId);
        List<CommentDto> result = commentService.getCommentsByAuthor(userId, from, size);
        return ResponseEntity.ok(result);
    }

    @DeleteMapping(USER_COMMENTS_ID)
    public ResponseEntity<Void> deleteComment(
            @PathVariable Long userId,
            @PathVariable Long commentId) {
        log.info(Message.DELETE_COMMENT, userId, commentId);
        commentService.deleteComment(userId, commentId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping(ADMIN_COMMENTS_ID)
    public ResponseEntity<Void> deleteCommentByAdmin(@PathVariable Long commentId) {
        log.info(Message.ADMIN_DELETE_COMMENT, commentId);
        commentService.deleteComment(commentId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping(COMMENTS_EVENT)
    public ResponseEntity<List<CommentDto>> getComments(
            @PathVariable Long eventId,
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.GET_COMMENTS, eventId);
        List<CommentDto> result = commentService.getComments(eventId, from, size);
        return ResponseEntity.ok(result);
    }

    @GetMapping(COMMENTS_ID)
    public ResponseEntity<CommentDto> getCommentById(@PathVariable Long commentId) {
        log.info(Message.GET_COMMENT_BY_ID, commentId);
        CommentDto result = commentService.getCommentById(commentId);
        return ResponseEntity.ok(result);
    }
}
