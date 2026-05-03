package ru.practicum.constant;

public class Message {
    //log.info

    //log.debug

    //log.warn
    public static final String GET_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Возвращаем дефолтного пользователя для id={}";
    public static final String GET_USERS_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Возвращаем пустой список. Причина: {}";
    //log.error
    public static final String REGISTER_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Регистрация невозможна. Причина: {}";
    public static final String DELETE_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Удаление пользователя {} невозможно";
    //exception
    public static final String USER_SERVICE_NOT_AVAILABLE = "Сервис пользователей временно недоступен";
}
