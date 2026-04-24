package main.java.ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.service.CommentService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.openapi.api.CommentApi;
import ru.practicum.openapi.model.CommentDto;
import ru.practicum.openapi.model.NewCommentDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CommentController implements CommentApi {
    private final CommentService commentService;

    @Override
    public ResponseEntity<CommentDto> addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info(Messages.MESSAGE_ADD_COMMENT, userId, eventId);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(commentService.addComment(userId, eventId, newCommentDto));
    }

    @Override
    public ResponseEntity<Void> deleteComment(Long userId, Long commentId) {
        log.info(Messages.MESSAGE_DELETE_COMMENT, userId, commentId);

        commentService.deleteComment(userId, commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<Void> deleteCommentByAdmin(Long commentId) {
        log.info(Messages.MESSAGE_DELETE_COMMENT_BY_ADMIN, commentId);

        commentService.deleteComment(commentId);

        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }

    @Override
    public ResponseEntity<CommentDto> getCommentById(Long commentId) {
        log.info(Messages.MESSAGE_GET_COMMENT_BY_ID, commentId);

        return ResponseEntity.ok(commentService.getCommentById(commentId));
    }

    @Override
    public ResponseEntity<List<CommentDto>> getComments(Long eventId, Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_COMMENTS_BY_EVENT, eventId);

        return ResponseEntity.ok(commentService.getComments(eventId, from, size));
    }

    @Override
    public ResponseEntity<List<CommentDto>> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_COMMENTS_BY_AUTHOR, userId);

        return ResponseEntity.ok(commentService.getCommentsByAuthor(userId, from, size));
    }

    @Override
    public ResponseEntity<CommentDto> updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        log.info(Messages.MESSAGE_UPDATE_COMMENT, userId, eventId, commentId);

        return ResponseEntity.ok(commentService.updateComment(userId, eventId, commentId, newCommentDto));
    }
}
