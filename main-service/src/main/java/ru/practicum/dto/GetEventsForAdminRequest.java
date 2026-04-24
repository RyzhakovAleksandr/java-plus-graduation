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
public class GetEventsForAdminRequest {
    List<Long> users;

    List<String> states;

    List<Long> categories;

    String rangeStart;

    String rangeEnd;

    Integer from;

    Integer size;
}
