package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.client.EventClient;
import ru.practicum.client.UserClient;
import ru.practicum.constant.Message;
import ru.practicum.dto.EventFullDto;
import ru.practicum.dto.UserDto;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.model.Comment;
import ru.practicum.repository.CommentRepository;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.EventShortDto;
import ru.practicum.dto.NewCommentDto;
import ru.practicum.dto.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;
    private final CommentMapper commentMapper;

    private final UserClient userClient;
    private final EventClient eventClient;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info(Message.LOG_ADD_COMMENT, userId, eventId);

        UserDto user = userClient.getUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format(Message.USER_NOT_FOUND, userId));
        }

        EventFullDto event = eventClient.getEvent(eventId);
        if (event == null) {
            throw new NotFoundException(String.format(Message.EVENT_NOT_FOUND, eventId));
        }

        if (!"PUBLISHED".equals(event.getState())) {
            throw new ValidationException(Message.COMMENT_ONLY_PUBLISHED);
        }

        if (newCommentDto.getText() == null || newCommentDto.getText().isBlank()) {
            throw new ValidationException(Message.COMMENT_CAN_NOT_EMPTY);
        }

        Comment comment = commentMapper.toComment(newCommentDto, userId, eventId);
        comment = commentRepository.save(comment);

        return toCommentDto(comment);
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        log.info(Message.LOG_UPDATE_COMMENT, commentId, userId, eventId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Message.COMMENT_NOT_FOUND, commentId)));

        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException(Message.COMMENT_NOT_YOU);
        }

        if (!comment.getEventId().equals(eventId)) {
            throw new ValidationException(Message.COMMENT_NOT_IS_EVENT);
        }

        if (newCommentDto.getText() == null || newCommentDto.getText().isBlank()) {
            throw new ValidationException(Message.COMMENT_CAN_NOT_EMPTY);
        }

        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        comment = commentRepository.save(comment);

        return toCommentDto(comment);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        log.info(Message.LOG_GET_COMMENTS_FOR_USER, userId);

        UserDto user = userClient.getUser(userId);
        if (user == null) {
            throw new NotFoundException(String.format(Message.USER_NOT_FOUND, userId));
        }

        return commentRepository.findAllByAuthorId(userId, PageRequest.of(from / size, size))
                .stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        log.info(Message.LOG_GET_COMMENTS_FOR_EVENT, eventId);

        EventFullDto event = eventClient.getEvent(eventId);
        if (event == null) {
            throw new NotFoundException(String.format(Message.COMMENT_NOT_FOUND, eventId));
        }

        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(this::toCommentDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CommentDto getCommentById(Long commentId) {
        log.info(Message.LOG_GET_COMMENT, commentId);
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Message.COMMENT_NOT_FOUND, commentId)));
        return toCommentDto(comment);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        log.info(Message.LOG_USER_DELETE_COMMENT, commentId, userId);

        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Message.COMMENT_NOT_FOUND, commentId)));

        if (!comment.getAuthorId().equals(userId)) {
            throw new ForbiddenException(Message.COMMENT_YOU_CAN_NOT_DELETE);
        }

        commentRepository.delete(comment);
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info(Message.LOG_ADMIN_DELETE_COMMENT, commentId);

        if (!commentRepository.existsById(commentId)) {
            throw new NotFoundException(String.format(Message.COMMENT_NOT_FOUND, commentId));
        }

        commentRepository.deleteById(commentId);
    }

    private CommentDto toCommentDto(Comment comment) {
        CommentDto dto = commentMapper.toCommentDto(comment);

        try {
            UserDto user = userClient.getUser(comment.getAuthorId());
            if (user != null) {
                dto.setAuthor(UserShortDto.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build());
            }
        } catch (Exception e) {
            log.warn(Message.NOT_GET_AUTHOR, e.getMessage());
        }

        try {
            EventShortDto event = eventClient.getEventShort(comment.getEventId());
            if (event != null) {
                dto.setEvent(event);
            }
        } catch (Exception e) {
            log.warn(Message.NOT_GET_EVENT, e.getMessage());
        }

        return dto;
    }
}
