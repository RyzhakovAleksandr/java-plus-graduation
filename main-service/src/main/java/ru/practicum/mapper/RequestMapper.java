package main.java.ru.practicum.mapper;

import main.java.ru.practicum.constant.Values;
import main.java.ru.practicum.persistence.entity.Request;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import org.mapstruct.Named;
import ru.practicum.openapi.model.ParticipationRequestDto;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = "spring")
public interface RequestMapper {
    DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern(Values.DATE_TIME_PATTERN)
                    .withZone(ZoneId.of("UTC"));

    @Mapping(target = "created", source = "created", qualifiedByName = "toOffsetDateTime")
    ParticipationRequestDto toParticipationRequestDto(Request participationRequest);

    @Named("toOffsetDateTime")
    default String toOffsetDateTime(OffsetDateTime time) {
        if (time == null) {
            return null;
        }

        ZonedDateTime utcTime = time.atZoneSameInstant(ZoneId.of("UTC"));
        return FORMATTER.format(utcTime);
    }
}
