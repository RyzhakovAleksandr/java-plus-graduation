package ru.practicum.constant;

public class Message {
    //log.info

    //log.debug

    //log.warn
    public static final String GET_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Возвращаем дефолтного пользователя для id={}";
    public static final String GET_USERS_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Возвращаем пустой список. Причина: {}";
    public static final String GET_CATEGORY_SERVICE_NOT_AVAILABLE = "category-service недоступен. Возвращаем дефолтную категорию для id={}";
    public static final String GET_CATEGORIES_SERVICE_NOT_AVAILABLE = "category-service недоступен. Возвращаем пустой список категорий";
    public static final String GET_REQUEST_SERVICE_NOT_AVAILABLE = "request-service недоступен. Возвращаем пустой список заявок пользователя {}";
    public static final String GET_REQUEST_EVENTS_SERVICE_NOT_AVAILABLE = "request-service недоступен. Возвращаем пустой список участников события {}";
    //log.error
    public static final String REGISTER_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Регистрация невозможна. Причина: {}";
    public static final String DELETE_USER_SERVICE_NOT_AVAILABLE = "user-service недоступен. Удаление пользователя {} невозможно";
    public static final String ADD_CATEGORY_SERVICE_NOT_AVAILABLE = "category-service недоступен. Добавление категории невозможно. Причина: {}";
    public static final String DELETE_CATEGORY_SERVICE_NOT_AVAILABLE = "category-service недоступен. Удаление категории {} невозможно";
    public static final String UPDATE_CATEGORY_SERVICE_NOT_AVAILABLE = "category-service недоступен. Обновление категории {} невозможно";
    public static final String ADD_REQUEST_SERVICE_NOT_AVAILABLE = "request-service недоступен. Невозможно создать заявку на событие {}";
    public static final String CANCEL_REQUEST_SERVICE_NOT_AVAILABLE = "request-service недоступен. Невозможно отменить заявку {}";
    public static final String CHANGE_REQUEST_SERVICE_NOT_AVAILABLE = "request-service недоступен. Невозможно изменить статус заявок для события {}";
    //exception
    public static final String USER_SERVICE_NOT_AVAILABLE = "Сервис пользователей временно недоступен";
    public static final String CATEGORY_SERVICE_NOT_AVAILABLE = "Сервис категорий временно недоступен";
    public static final String REQUEST_SERVICE_NOT_AVAILABLE = "Сервис заявок временно недоступен";
}
