package main.java.ru.practicum.exception;

import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;

import org.hibernate.exception.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import ru.practicum.openapi.model.ApiError;

import java.time.LocalDateTime;
import java.util.Arrays;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler({NotFoundCompletion.class})
    public ResponseEntity<ApiError> handleNotFoundCompletion(NotFoundCompletion ex) {
        log.warn(Messages.NOT_FOUND_COMPLETION, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.NOT_FOUND_COMPLETION,
                Messages.NOT_FOUND_COMPLETION,
                ApiError.StatusEnum._404_NOT_FOUND,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(body);
    }

    @ExceptionHandler({MissingServletRequestParameterException.class})
    public ResponseEntity<ApiError> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        log.warn(Messages.MESSAGE_MISSING_SERVLET_REQUEST_PARAMETER, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Messages.MESSAGE_MISSING_SERVLET_REQUEST_PARAMETER,
                Messages.MESSAGE_MISSING_SERVLET_REQUEST_PARAMETER,
                ApiError.StatusEnum._400_BAD_REQUEST,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ResponseEntity<ApiError> handleMethodArgumentTypeMismatchException(MethodArgumentTypeMismatchException ex) {
        log.warn(Messages.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
                Messages.METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION,
                ApiError.StatusEnum._400_BAD_REQUEST,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }

    @ExceptionHandler({ForbiddenException.class})
    public ResponseEntity<ApiError> handleForbiddenException(ForbiddenException ex) {
        log.warn(Exceptions.EXCEPTION_REQUEST_EXIST, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.EXCEPTION_REQUEST_EXIST,
                Exceptions.EXCEPTION_REQUEST_EXIST,
                ApiError.StatusEnum._409_CONFLICT,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler({NotMeetRulesEditionException.class})
    public ResponseEntity<ApiError> handleNotMeetRulesEditionException(NotMeetRulesEditionException ex) {
        log.warn(Exceptions.EXCEPTION_NOT_MEET_RULES, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.EXCEPTION_NOT_MEET_RULES,
                Exceptions.EXCEPTION_NOT_MEET_RULES,
                ApiError.StatusEnum._409_CONFLICT,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ApiError> handleConstraintViolationException(ConstraintViolationException ex) {
        log.warn(Messages.CONSTRAINT_VIOLATION_EXCEPTION, ex);
        ApiError body = new ApiError(Arrays.stream(
                ex.getStackTrace()).map(String::valueOf).toList(),
                Exceptions.CONSTRAINT_VIOLATION_EXCEPTION,
                Messages.CONSTRAINT_VIOLATION_EXCEPTION,
                ApiError.StatusEnum._409_CONFLICT,
                LocalDateTime.now().toString());

        return ResponseEntity.status(HttpStatus.CONFLICT).body(body);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleIllegalArgumentException(IllegalArgumentException  e) {
        log.info(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT)
                .message(Exceptions.EXCEPTION_NOT_ILLEGAL_ARGUMENT)
                .status(ApiError.StatusEnum._400_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleHttpMessageNotReadableException(HttpMessageNotReadableException  e) {
        log.info(Messages.MESSAGE_NOT_READABLE, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_READABLE)
                .message(Exceptions.EXCEPTION_NOT_READABLE)
                .status(ApiError.StatusEnum._400_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleMethodArgumentNotValidException(MethodArgumentNotValidException  e) {
        log.info(Messages.MESSAGE_NOT_VALID, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_VALID)
                .message(Exceptions.EXCEPTION_NOT_VALID)
                .status(ApiError.StatusEnum._400_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiError> handleNotFoundException(NotFoundException e) {
        log.info(Messages.MESSAGE_NOT_FOUND, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_FOUND)
                .message(Exceptions.EXCEPTION_NOT_FOUND)
                .status(ApiError.StatusEnum._404_NOT_FOUND)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(error);
    }

    @ExceptionHandler(MismatchDateException.class)
    public ResponseEntity<ApiError> handleMismatchDateException(MismatchDateException e) {
        log.info(Messages.MESSAGE_DATE_MISMATCH, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_DATE_MISMATCH)
                .message(Exceptions.EXCEPTION_DATE_MISMATCH)
                .status(ApiError.StatusEnum._400_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(LimitRequestsExceededException.class)
    public ResponseEntity<ApiError> handleLimitRequestsExceededException(LimitRequestsExceededException e) {
        log.info(Messages.MESSAGE_LIMIT_EXCEEDED, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_LIMIT_EXCEEDED)
                .message(Exceptions.EXCEPTION_LIMIT_EXCEEDED)
                .status(ApiError.StatusEnum._409_CONFLICT)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiError> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        log.info(Exceptions.EXCEPTION_DATA_INTEGRITY_VIOLATION, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Exceptions.EXCEPTION_DATA_INTEGRITY_VIOLATION)
                .message(Exceptions.EXCEPTION_DATA_INTEGRITY_VIOLATION)
                .status(ApiError.StatusEnum._409_CONFLICT)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(NotRespondStatusException.class)
    public ResponseEntity<ApiError> handleNotRespondStatusException(NotRespondStatusException e) {
        log.info(Messages.MESSAGE_NOT_RESPOND_STATUS, e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_NOT_RESPOND_STATUS)
                .message(Exceptions.EXCEPTION_NOT_RESPOND_STATUS)
                .status(ApiError.StatusEnum._409_CONFLICT)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.CONFLICT).body(error);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleException(Exception e) {
        log.info(Messages.MESSAGE_INTERNAL_SERVER, e.getMessage(), e);

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason(Messages.MESSAGE_INTERNAL_SERVER)
                .message(Exceptions.EXCEPTION_INTERNAL_SERVER)
                .status(ApiError.StatusEnum._500_INTERNAL_SERVER_ERROR)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity<ApiError> handleValidationException(ValidationException e) {
        log.info("Ошибка валидации: {}", e.getMessage());

        ApiError error = ApiError.builder()
                .errors(Arrays.stream(e.getStackTrace()).map(String::valueOf).toList())
                .reason("Некорректно составлен запрос.")
                .message(e.getMessage())
                .status(ApiError.StatusEnum._400_BAD_REQUEST)
                .timestamp(LocalDateTime.now().toString())
                .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}
