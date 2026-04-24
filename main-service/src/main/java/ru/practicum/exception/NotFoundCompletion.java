package main.java.ru.practicum.exception;

public class NotFoundCompletion extends RuntimeException {
    public NotFoundCompletion(String message) {
        super(message);
    }
}
