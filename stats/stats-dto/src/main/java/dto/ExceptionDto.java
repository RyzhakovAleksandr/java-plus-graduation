package dto;

import com.fasterxml.jackson.annotation.JsonFormat;

import constant.Values;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.experimental.FieldDefaults;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class ExceptionDto {
    String message;

    Integer code;

    @JsonFormat(pattern = Values.DATE_TIME_PATTERN)
    LocalDateTime timestamp;
}
