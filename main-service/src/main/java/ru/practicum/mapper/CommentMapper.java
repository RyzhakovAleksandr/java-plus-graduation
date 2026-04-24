package main.java.ru.practicum.mapper;

import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.persistence.entity.Comment;
import main.java.ru.practicum.persistence.entity.Event;
import main.java.ru.practicum.persistence.entity.User;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.practicum.openapi.model.CommentDto;
import ru.practicum.openapi.model.EventShortDto;
import ru.practicum.openapi.model.NewCommentDto;
import ru.practicum.openapi.model.UserShortDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring", uses = {UserMapper.class, EventMapper.class})
public interface CommentMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "text", source = "newCommentDto.text")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "edited", ignore = true)
    Comment toComment(NewCommentDto newCommentDto, User author, Event event);

    @Mapping(target = "id", source = "comment.id")
    @Mapping(target = "text", source = "comment.text")
    @Mapping(target = "created", source = "comment.created", qualifiedByName = "localDateTimeToString")
    @Mapping(target = "edited", source = "comment.edited", qualifiedByName = "localDateTimeToString")
    CommentDto toCommentDto(Comment comment, UserShortDto author, EventShortDto event);

    @Named("localDateTimeToString")
    default String localDateTimeToString(LocalDateTime time) {
        if (time == null) {
            return null;
        }

        return time.format(FORMATTER);
    }
}