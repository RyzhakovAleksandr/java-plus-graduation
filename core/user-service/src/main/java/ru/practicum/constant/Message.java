package ru.practicum.constant;

public class Message {

    //log.info
    public static final String MESSAGE_REGISTER_USER = "POST /admin/users - регистрация пользователя: {}";
    public static final String MESSAGE_GET_LIST_USERS = "GET /admin/users - ids={}, from={}, size={}";
    public static final String MESSAGE_DELETE_USER = "DELETE /admin/users/{}";
    public static final String MESSAGE_GET_USER = "GET /users/{}";

    //exception
    public static final String EXCEPTION_CONFLICT_EMAIL = "Пользователь с email %s уже существует";
    public static final String EXCEPTION_NOT_FOUND_USER = "Пользователь с id=%d не найден";
    public static final String EXCEPTION_EMAIL_MUST_BE = "Email обязателен для заполнения";
    public static final String EXCEPTION_EMAIL_EMPTY = "Email не может быть пустым или состоять только из пробелов";
    public static final String EXCEPTION_EMAIL_TOO_LONG = "Email не может превышать 254 символа";
    public static final String EXCEPTION_EMAIL_NOT_CORRECT = "Некорректный формат email";
    public static final String EXCEPTION_LOCAL_TOO_LONG = "Локальная часть email не может превышать 64 символа";
    public static final String EXCEPTION_DOMAIN_TOO_LONG = "Доменная часть email не может превышать 253 символа";
    public static final String EXCEPTION_PATH_DOMAIN_TOO_LONG = "Каждая часть домена не может превышать 63 символа";
    public static final String EXCEPTION_PATH_DOMAIN_NO_CORRECT = "Части домена не могут начинаться или заканчиваться дефисом";
    public static final String EXCEPTION_NAME_MUST_BE = "Имя обязательно для заполнения";
    public static final String EXCEPTION_NAME_EMPTY = "Имя не может быть пустым или состоять только из пробелов";
    public static final String EXCEPTION_NAME_TOO_SMALL = "Имя должно содержать минимум 2 символа";
    public static final String EXCEPTION_NAME_TOO_LONG = "Имя должно содержать не более 250 символов";
}
