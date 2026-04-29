package ru.practicum.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import ru.practicum.constant.Exceptions;
import ru.practicum.constant.Messages;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CommentMapper;
import ru.practicum.mapper.EventMapper;
import ru.practicum.mapper.UserMapper;
import ru.practicum.persistence.entity.Comment;
import ru.practicum.persistence.entity.Event;
import ru.practicum.persistence.entity.Request;
import ru.practicum.persistence.entity.User;
import ru.practicum.persistence.repository.CommentRepository;
import ru.practicum.persistence.repository.EventRepository;
import ru.practicum.persistence.repository.RequestRepository;
import ru.practicum.persistence.repository.UserRepository;
import ru.practicum.persistence.status.StatusRequest;

import org.springframework.stereotype.Service;
import org.springframework.data.domain.PageRequest;
import org.springframework.transaction.annotation.Transactional;

import ru.practicum.openapi.model.CommentDto;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewCommentDto;
import ru.practicum.openapi.model.UserShortDto;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ru.practicum.openapi.model.EventFullDto.StateEnum.PUBLISHED;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class CommentServiceImpl implements CommentService {
    private final CommentRepository commentRepository;

    private final UserRepository userRepository;

    private final EventRepository eventRepository;

    private final RequestRepository requestRepository;

    private final CommentMapper commentMapper;

    private final UserMapper userMapper;

    private final EventMapper eventMapper;

    @Override
    public CommentDto addComment(Long userId, Long eventId, NewCommentDto newCommentDto) {
        log.info(Messages.MESSAGE_ADD_COMMENT, userId, eventId);

        if (newCommentDto.getText().isBlank()) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_IS_EMPTY);
        }

        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);

        if (!event.getState().equals(PUBLISHED.toString())) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_NOT_PUBLISHED);
        }

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());

        return commentMapper.toCommentDto(commentRepository.save(commentMapper.toComment(newCommentDto, author, event)),
                userMapper.userToUserShortDto(author), createEventShortDtoWithConfirmedRequests(event, confirmedRequests));
    }

    @Override
    public CommentDto updateComment(Long userId, Long eventId, Long commentId, NewCommentDto newCommentDto) {
        log.info(Messages.MESSAGE_UPDATE_COMMENT, userId, eventId, commentId);

        if (newCommentDto.getText().isBlank()) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_IS_EMPTY);
        }

        User author = checkAndGetUser(userId);
        Event event = checkAndGetEvent(eventId);
        Comment comment = checkAndGetComment(commentId);

        if (!comment.getAuthor().getId().equals(userId)) {
            throw new ValidationException(Exceptions.EXCEPTION_ONLY_AUTHOR_CAN_EDIT);
        }

        if (!comment.getEvent().getId().equals(eventId)) {
            throw new ValidationException(Exceptions.EXCEPTION_COMMENT_FOR_OTHER_EVENT);
        }

        comment.setText(newCommentDto.getText());
        comment.setEdited(LocalDateTime.now());
        comment = commentRepository.save(comment);

        Long confirmedRequests = requestRepository.countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString());

        return commentMapper.toCommentDto(comment, userMapper.userToUserShortDto(author),
                createEventShortDtoWithConfirmedRequests(event, confirmedRequests));
    }

    @Override
    @Transactional(readOnly = true)
    public List<CommentDto> getCommentsByAuthor(Long userId, Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_COMMENTS_BY_AUTHOR, userId);

        checkAndGetUser(userId);
        List<Comment> comments = commentRepository.findAllByAuthorId(
                userId,
                PageRequest.of(from / size, size)
        );

        if (comments.isEmpty()) {
            return List.of();
        }

        List<Long> eventIds = comments.stream()
                .map(comment -> comment.getEvent().getId())
                .distinct()
                .collect(Collectors.toList());

        Map<Long, Long> confirmedRequestsCountMap = requestRepository
                .getConfirmedRequestsCountMap(eventIds, StatusRequest.CONFIRMED.toString());

        return comments.stream()
                .map(comment -> {
                    Long eventId = comment.getEvent().getId();

                    Long confirmedRequests = confirmedRequestsCountMap.getOrDefault(eventId, 0L);

                    UserShortDto userShort = userMapper.userToUserShortDto(comment.getAuthor());
                    EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                            comment.getEvent(), confirmedRequests);

                    return commentMapper.toCommentDto(comment, userShort, eventShort);
                })
                .collect(Collectors.toList());
    }

    @Override
    public List<CommentDto> getComments(Long eventId, Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_COMMENTS_BY_EVENT, eventId);

        Event event = checkAndGetEvent(eventId);

        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(event, requestRepository
                .countByEventIdAndStatus(eventId, StatusRequest.CONFIRMED.toString()));

        return commentRepository.findAllByEventId(eventId, PageRequest.of(from / size, size))
                .stream()
                .map(comment -> commentMapper
                        .toCommentDto(comment, userMapper.userToUserShortDto(comment.getAuthor()), eventShort))
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getCommentById(Long commentId) {
        log.info(Messages.MESSAGE_GET_COMMENT_BY_ID, commentId);

        Comment comment = checkAndGetComment(commentId);
        Long confirmedRequests = requestRepository.countByEventIdAndStatus(
                comment.getEvent().getId(), StatusRequest.CONFIRMED.toString());
        EventShortDto eventShort = createEventShortDtoWithConfirmedRequests(
                comment.getEvent(), confirmedRequests);

        return commentMapper.toCommentDto(comment, userMapper.userToUserShortDto(comment.getAuthor()), eventShort);
    }

    @Override
    public void deleteComment(Long userId, Long commentId) {
        log.info(Messages.MESSAGE_DELETE_COMMENT, userId, commentId);

        checkAndGetUser(userId);

        if (!checkAndGetComment(commentId).getAuthor().getId().equals(userId)) {
            throw new ValidationException(Exceptions.EXCEPTION_ONLY_AUTHOR_CAN_DELETE);
        }

        commentRepository.deleteById(commentId);
    }

    @Override
    public void deleteComment(Long commentId) {
        log.info(Messages.MESSAGE_DELETE_COMMENT_BY_ADMIN, commentId);

        checkAndGetComment(commentId);
        commentRepository.deleteById(commentId);
    }

    private User checkAndGetUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_NOT_FOUND_USER, userId)));
    }

    private Event checkAndGetEvent(Long eventId) {
        return eventRepository.findById(eventId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_EVENT_NOT_FOUND, eventId)));
    }

    private Comment checkAndGetComment(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException(String.format(Exceptions.EXCEPTION_COMMENT_NOT_FOUND, commentId)));
    }

    private EventShortDto createEventShortDtoWithConfirmedRequests(Event event, Long confirmedRequests) {
        if (event == null) {
            return null;
        }

        EventShortDto dto = eventMapper.eventToEventShortDto(event)
                .confirmedRequests(confirmedRequests)
                .views(event.getViews() != null ? event.getViews() : 0L);

        return dto;
    }
}
