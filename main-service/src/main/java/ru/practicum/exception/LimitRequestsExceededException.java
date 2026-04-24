package main.java.ru.practicum.exception;

public class LimitRequestsExceededException extends RuntimeException {
    public LimitRequestsExceededException(String message) {
        super(message);
    }
}
