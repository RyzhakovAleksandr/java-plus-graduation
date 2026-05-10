package ru.practicum.constant;

public class Message {
    //log.info
    public static final String LOG_ADD_REQUEST = "Добавление заявки от пользователя {} на событие {}";
    public static final String LOG_CREATED_REQUEST = "Создана заявка {} со статусом {}";
    public static final String LOG_UPDATE_REQUEST = "Обновление статуса заявок для события {} пользователем {}";
    public static final String LOG_CONFIRMED_REQUEST = "Подтверждено заявок: {}, отклонено: {}";
    public static final String LOG_CANCEL_REQUEST = "Отмена заявки {} пользователем {}";
    public static final String LOG_REQUESTS_BY_USER = "Получение заявок пользователя {}";
    public static final String LOG_GET_REQUEST_INITIATOR = "Получение заявок на событие {} инициатором {}";
    public static final String GET_USER_REQUEST = "GET /users/{}/requests";
    public static final String ADD_REQUEST = "POST /users/{}/requests?eventId={}";
    public static final String CANCEL_REQUEST = "PATCH /users/{}/requests/{}/cancel";
    public static final String GET_REQUEST = "GET /users/{}/events/{}/requests";
    public static final String UPDATE_REQUEST = "PATCH /users/{}/events/{}/requests";
    //exception
    public static final String USER_NOT_FOUND = "Пользователь с id=%d не найден";
    public static final String EVENT_NOT_FOUND = "Событие с id=&d не найдено";
    public static final String REQUEST_NOT_FOUND = "Заявка не найдена";
    public static final String EVENT_NOT_PUBLISHED = "Нельзя участвовать в неопубликованном событии";
    public static final String INITIATOR_CAN_NOT_BE = "Инициатор события не может подать заявку на участие";
    public static final String EXISTING_REQUEST = "Заявка уже существует";
    public static final String LIMIT_REQUEST = "Достигнут лимит участников";
    public static final String ONLY_INITIATOR_CAN_CHANGE = "Только инициатор может изменять статус заявок";
    public static final String ONLY_INITIATOR_CAN_VIEW = "Только инициатор события может просматривать заявки";
}
