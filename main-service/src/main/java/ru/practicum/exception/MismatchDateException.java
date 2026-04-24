package main.java.ru.practicum.exception;

public class MismatchDateException extends RuntimeException {
    public MismatchDateException(String message) {
        super(message);
    }
}
