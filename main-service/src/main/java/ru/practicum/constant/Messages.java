package main.java.ru.practicum.constant;

public class Messages {
    public static final String GET_COMPILATIONS = "Пришел запрос на получение подборки событий";
    public static final String GET_COMPILATION = "Пришел запрос на получение подборки события по id {}";
    public static final String NOT_FOUND_COMPLETION = "Подборка событий по id не найдено";
    public static final String METHOD_ARGUMENT_TYPE_MISMATCH_EXCEPTION = "Ошибка валидации";
    public static final String SAVE_COMPILATION = "Пришел запрос на сохранение подборки события {}";
    public static final String CONSTRAINT_VIOLATION_EXCEPTION = "Не удалось выполнить SQL запрос";
    public static final String DELETE_COMPILATION = "Пришел запрос на удаление подборки события {}";
    public static final String UPDATE_COMPILATION = "Пришел запрос на обновление подборки события {}";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {}";
    public static final String MESSAGE_NOT_VALID = "Значение не прошло валидацию: {}";
    public static final String MESSAGE_NOT_READABLE = "Тело запроса не читаемо: {}";
    public static final String MESSAGE_CATEGORY_NOT_FOUND = "Категория с id=%d не найдена";
    public static final String MESSAGE_NOT_FOUND = "Требуемый объект не был найден.";
    public static final String MESSAGE_ADD_CATEGORIES = "POST /admin/categories with request: {}";
    public static final String MESSAGE_DELETE_CATEGORIES = "DELETE /admin/categories/{}";
    public static final String MESSAGE_GET_CATEGORIES = "GET /categories";
    public static final String MESSAGE_GET_CATEGORY = "GET /categories/{}";
    public static final String MESSAGE_UPDATE_CATEGORY = "PATCH /admin/categories/{} with request: {}";
    public static final String MESSAGE_DELETE_USER = "DELETE /admin.users/{}";
    public static final String MESSAGE_GET_USERS = "GET /admin/users?ids={}&from={}&size={}";
    public static final String MESSAGE_REGISTER_USER = "POST /admin/users with request: {}";
    public static final String MESSAGE_ADD_EVENT = "POST /users/{}/events users with request: {}";
    public static final String MESSAGE_DATE_MISMATCH = "Дата не соответствует требованиям";
    public static final String MESSAGE_CHANGE_STATUS = "Поступил запрос на изменения статуса";
    public static final String MESSAGE_LIMIT_EXCEEDED = "Лимит запросов превышен";
    public static final String MESSAGE_NOT_RESPOND_STATUS = "Статус запросов должен быть PENDING";
    public static final String MESSAGE_GET_EVENT_BY_ID = "Поступил запрос на получение события по id: {}";
    public static final String MESSAGE_GET_PARTICIPANTS =
            "Поступил запрос на получение информации о запросах от пользователя: {} для события: {}";
    public static final String MESSAGE_GET_EVENTS_BY_USER_ID_AND_EVENT_ID =
            "Поступил запрос на получение информации о событиии: {} от пользователя: {}";
    public static final String MESSAGE_GET_EVENTS = "Поступил запрос на получение события";
    public static final String MESSAGE_GET_EVENTS_FOR_ADMIN = "Поступил запрос на получение события администратором";
    public static final String MESSAGE_GET_EVENTS_FOR_USER = "Поступил запрос на получение события пользователем";
    public static final String MESSAGE_UPDATE_EVENT = "Поступил запрос на обновление события";
    public static final String MESSAGE_ADDED_REQUEST = "Заявка создана успешно. ID заявки: {}, статус: {}";
    public static final String MESSAGE_CANCEL_REQUEST = "Заявка {} успешно отменена пользователем {}";
    public static final String MESSAGE_GET_REQUESTS = "Найдено {} заявок для пользователя {}";
    public static final String MESSAGE_MISSING_SERVLET_REQUEST_PARAMETER = "Несоответствие параметров при запросе";
    public static final String MESSAGE_ADD_COMMENT = "Добавление комментария пользователя {} к событию {}";
    public static final String MESSAGE_UPDATE_COMMENT = "Обновление комментария пользователя {} для события {}, комментарий {}";
    public static final String MESSAGE_GET_COMMENTS_BY_AUTHOR = "Получение комментариев от автора {}";
    public static final String MESSAGE_GET_COMMENTS_BY_EVENT = "Получение комментариев к событию {}";
    public static final String MESSAGE_GET_COMMENT_BY_ID = "Получение комментария по идентификатору {}";
    public static final String MESSAGE_DELETE_COMMENT = "Удаление комментария {} пользователем {}";
    public static final String MESSAGE_DELETE_COMMENT_BY_ADMIN = "Удаление комментария {} администратором";
}
