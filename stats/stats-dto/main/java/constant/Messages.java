package constant;

public class Messages {
    public static final String INFORMATION_ADDED = "Информация сохранена";
    public static final String POST_HIT_REQUEST = "POST /hit: app={}, uri={}, ip={}";
    public static final String GET_STATS_REQUEST = "GET /stats: start={}, end={}, uris={}, unique={}";
    public static final String DATE_EXCEPTION = "Дата От может быть только после даты До";
    public static final String EXCEPTION_INTERNAL_SERVER = "Внутренняя ошибка сервера. Подождите несколько минут и попробуйте снова.";
    public static final String MESSAGE_INTERNAL_SERVER = "Внутренняя ошибка сервера: {} ";
    public static final String EXCEPTION_UNPROCESSABLE_ENTITY = "Недопустимый аргумент.";
    public static final String MESSAGE_UNPROCESSABLE_ENTITY = "Некорректный аргумент: {}";
    public static final String EXCEPTION_NOT_VALID = "Валидация не пройдена. Недопустимое значение.";
    public static final String MESSAGE_NOT_VALID = "Значение не прошло валидацию: {}";
    public static final String EXCEPTION_NOT_READABLE = "Тело запроса не читаемо.";
    public static final String MESSAGE_NOT_READABLE = "Тело запроса не читаемо: {}";
    public static final String EXCEPTION_CONSTRAINT_VIOLATION = "Недопустимое значение.";
    public static final String MESSAGE_CONSTRAINT_VIOLATION = "Недопустимое значение: {}";
}
