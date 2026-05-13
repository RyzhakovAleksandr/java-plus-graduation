package ru.practicum.mapper;

import ru.practicum.constant.Values;
import ru.practicum.model.Comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import ru.practicum.dto.CommentDto;
import ru.practicum.dto.NewCommentDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface CommentMapper {

    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "authorId", source = "authorId")
    @Mapping(target = "eventId", source = "eventId")
    @Mapping(target = "created", expression = "java(java.time.LocalDateTime.now())")
    @Mapping(target = "edited", ignore = true)
    Comment toComment(NewCommentDto newCommentDto, Long authorId, Long eventId);

    @Mapping(target = "created", source = "created", qualifiedByName = "formatDateTime")
    @Mapping(target = "edited", source = "edited", qualifiedByName = "formatDateTime")
    CommentDto toCommentDto(Comment comment);

    @Named("formatDateTime")
    default String formatDateTime(LocalDateTime dateTime) {
        if (dateTime == null) return null;
        return dateTime.format(FORMATTER);
    }
}