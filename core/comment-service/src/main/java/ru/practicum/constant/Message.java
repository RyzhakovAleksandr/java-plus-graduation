package ru.practicum.constant;

public class Message {
    //log.info
    public static final String LOG_ADD_COMMENT = "Добавление комментария от пользователя {} к событию {}";
    public static final String LOG_UPDATE_COMMENT = "Обновление комментария {} пользователем {} к событию {}";
    public static final String LOG_GET_COMMENTS_FOR_USER = "Получение комментариев пользователя {}";
    public static final String LOG_GET_COMMENTS_FOR_EVENT = "Получение комментариев события {}";
    public static final String LOG_GET_COMMENT = "Получение комментария {}";
    public static final String LOG_USER_DELETE_COMMENT = "Удаление комментария {} пользователем {}";
    public static final String LOG_ADMIN_DELETE_COMMENT = "Удаление комментария {} администратором";
    public static final String ADD_COMMENT = "POST /user/{}/comments/{}";
    public static final String UPDATE_COMMENT = "PATCH /user/{}/comments/{}/{}";
    public static final String GET_COMMENTS_BY_AUTHOR = "GET /user/{}/comments";
    public static final String DELETE_COMMENT = "DELETE /user/{}/comments/{}";
    public static final String ADMIN_DELETE_COMMENT = "DELETE /admin/comments/{}";
    public static final String GET_COMMENTS = "GET /comments/event/{}";
    public static final String GET_COMMENT_BY_ID = "GET /comments/{}";
    //log.warn
    public static final String NOT_GET_AUTHOR = "Не удалось получить автора комментария: {}";
    public static final String NOT_GET_EVENT = "Не удалось получить событие для комментария: {}";
    //exception
    public static final String USER_NOT_FOUND = "Пользователь с id=%d не найден";
    public static final String EVENT_NOT_FOUND = "Событие с id=%d не найдено";
    public static final String COMMENT_NOT_FOUND = "Комментарий с id=%d не найден";
    public static final String COMMENT_ONLY_PUBLISHED = "Комментарии можно оставлять только к опубликованным событиям";
    public static final String COMMENT_CAN_NOT_EMPTY = "Текст комментария не может быть пустым";
    public static final String COMMENT_NOT_YOU = "Редактировать комментарий может только автор";
    public static final String COMMENT_YOU_CAN_NOT_DELETE = "Удалить комментарий может только автор";
    public static final String COMMENT_NOT_IS_EVENT = "Комментарий не относится к указанному событию";
}
