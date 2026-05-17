package ru.practicum.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EventFullDto {
    Long id;
    String annotation;
    CategoryDto category;
    Long confirmedRequests;
    String createdOn;
    String description;
    String eventDate;
    UserShortDto initiator;
    Location location;
    Boolean paid;
    Integer participantLimit;
    String publishedOn;
    Boolean requestModeration;
    String state;
    String title;
    Double rating;
}
