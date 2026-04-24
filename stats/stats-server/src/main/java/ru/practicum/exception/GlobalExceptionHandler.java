package main.java.ru.practicum.exception;

import constant.Messages;

import dto.ExceptionDto;

import lombok.extern.slf4j.Slf4j;

import org.hibernate.exception.ConstraintViolationException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ExceptionDto> handleIllegalArgumentException(IllegalArgumentException e) {
        log.error(Messages.MESSAGE_UNPROCESSABLE_ENTITY, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_UNPROCESSABLE_ENTITY,
                        HttpStatus.UNPROCESSABLE_ENTITY.value(),
                        LocalDateTime.now()),
                HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ResponseStatusException.class)
    public ResponseEntity<ExceptionDto> handleResponseStatusException(ResponseStatusException  e) {
        log.error(Messages.MESSAGE_UNPROCESSABLE_ENTITY, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.DATE_EXCEPTION,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException  e) {
        log.error(Messages.MESSAGE_NOT_VALID, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_NOT_VALID,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ExceptionDto> handleHttpMessageNotReadableException(HttpMessageNotReadableException  e) {
        log.error(Messages.MESSAGE_NOT_READABLE, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_NOT_READABLE,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ExceptionDto> handleMethodArgumentTypeMismatchException(Exception  e) {
        log.error(Messages.EXCEPTION_NOT_VALID, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_NOT_VALID,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ExceptionDto> handleMissingServletRequestParameterException(Exception  e) {
        log.error(Messages.EXCEPTION_NOT_VALID, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_NOT_VALID,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<ExceptionDto> handleConstraintViolationException(ConstraintViolationException  e) {
        log.error(Messages.MESSAGE_CONSTRAINT_VIOLATION, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_CONSTRAINT_VIOLATION,
                        HttpStatus.BAD_REQUEST.value(),
                        LocalDateTime.now()),
                HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionDto> handleGlobalException(Exception e) {
        log.error(Messages.MESSAGE_INTERNAL_SERVER, e.getMessage());

        return new ResponseEntity<>(
                new ExceptionDto(Messages.EXCEPTION_INTERNAL_SERVER,
                        HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        LocalDateTime.now()),
                HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
