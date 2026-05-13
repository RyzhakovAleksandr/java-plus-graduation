package ru.practicum.constant;

public class Message {
    //log.info
    public static final String LOG_ADD_EVENT = "POST /users/{}/events";
    public static final String LOG_CHANGE_REQUEST_STATUS = "PATCH /users/{}/events/{}/requests";
    public static final String LOG_GET_EVENT = "GET /events/{}";
    public static final String LOG_GET_EVENT_PARTICIPANTS = "GET /users/{}/events/{}/requests";
    public static final String LOG_GET_EVENT_USER = "GET /users/{}/events/{}";
    public static final String LOG_GET_EVENTS = "GET /events";
    public static final String LOG_GET_ADMIN_EVENTS = "GET /admin/events";
    public static final String LOG_GET_EVENTS_USER = "GET /users/{}/events";
    public static final String LOG_UPDATE_EVENT = "PATCH /users/{}/events/{}";
    public static final String LOG_UPDATE_EVENT_ADMIN = "PATCH /admin/events/{}";
    public static final String MESSAGE_ADD_EVENT = "POST /users/{}/events users with request: {}";
    public static final String MESSAGE_CHANGE_STATUS = "Поступил запрос на изменения статуса";
    public static final String MESSAGE_GET_EVENT_BY_ID = "Поступил запрос на получение события по id: {}";
    public static final String MESSAGE_GET_PARTICIPANTS =
            "Поступил запрос на получение информации о запросах от пользователя: {} для события: {}";
    public static final String MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID =
            "Поступил запрос на получение информации о событии: {} от пользователя: {}";
    public static final String MESSAGE_GET_EVENTS = "Поступил запрос на получение события";
    public static final String MESSAGE_GET_EVENTS_FOR_ADMIN = "Поступил запрос на получение события администратором";
    public static final String MESSAGE_GET_EVENTS_FOR_USER = "Поступил запрос на получение события пользователем";
    public static final String MESSAGE_UPDATE_EVENT = "Поступил запрос на обновление события";
    public static final String MESSAGE_COUNT_CONFIRMED = "Event {} confirmedCount = {}";
    public static final String GET_EVENT = "Получение события {} (internal, без проверки публикации)";
    //log.warm
    public static final String CAN_NOT_SEND_STATUS = "Не удалось отправить статистику: {}";
    public static final String CAN_NOT_GET_CONFIRMED_REQUEST = "Failed to get confirmed requests for event {}: {}";
    //exception
    public static final String EXCEPTION_USER_NOT_FOUND = "Пользователь с id=%d не найден";
    public static final String EXCEPTION_CATEGORY_NOT_FOUND = "Категория с id=%d не найдена";
    public static final String EXCEPTION_EVENT_NOT_FOUND = "Событие с id=%d не найдено";
    public static final String EXCEPTION_NOT_FOUND = "Объект не найден, 404";
    public static final String EXCEPTION_DATE_MISMATCH = "Дата не соответствует требованиям 409";
    public static final String EXCEPTION_NOT_PUBLISHED = "Событие не найдено или недоступно";
    public static final String EXCEPTION_CANT_UPDATE_PUBLISHED = "Нельзя изменить опубликованное событие";
    public static final String EXCEPTION_LOCAL_NOT_FOUND = "Локация не найдена";
    public static final String EXCEPTION_NOT_MEET_RULES = "Не соответствует требованиям.";
    public static final String EXCEPTION_FIELD_ANNOTATION_NOT_HAS_SPACE = "Поле annotation не может состоять только из пробелов";
    public static final String EXCEPTION_FIELD_DESCRIPTION_NOT_HAS_SPACE = "Поле description не может состоять только из пробелов";
    public static final String EXCEPTION_FIELD_TITLE_NOT_HAS_SPACE = "Поле title не может состоять только из пробелов";
    public static final String EXCEPTION_WRONG_DATE_RANGE = "Дата начала диапазона не может быть позже даты окончания";
}
