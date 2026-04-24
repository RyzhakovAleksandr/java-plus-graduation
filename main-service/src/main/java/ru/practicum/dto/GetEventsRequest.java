package main.java.ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class GetEventsRequest {
    String text;

    List<Long> categories;

    Boolean paid;

    String rangeStart;

    String rangeEnd;

    Boolean onlyAvailable;

    String sort;

    Integer from;

    Integer size;
}
