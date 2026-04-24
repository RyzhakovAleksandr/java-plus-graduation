package main.java.ru.practicum.exception;

public class NotRespondStatusException extends RuntimeException {
    public NotRespondStatusException(String message) {
        super(message);
    }
}
