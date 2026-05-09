package ru.practicum.constant;

public class Message {
    //log.info
    public static final String LOG_GET_COMPILATIONS = "Получение подборок, pinned={}, from={}, size={}";
    public static final String LOG_GET_COMPILATION = "Получение подборки с id={}";
    public static final String LOG_SAVE_COMPILATION = "Сохранение подборки: {}";
    public static final String LOG_UPDATE_COMPILATION = "Обновление подборки с id={}, request={}";
    public static final String LOG_DELETE_COMPILATION = "Удаление подборки с id={}";
    public static final String GET_COMPILATIONS = "GET /compilations?pinned={}&from={}&size={}";
    public static final String GET_COMPILATION = "GET /compilations/{}";
    public static final String SAVE_COMPILATION = "POST /admin/compilations";
    public static final String UPDATE_COMPILATION = "PATCH /admin/compilations/{}";
    public static final String DELETE_COMPILATION = "DELETE /admin/compilations/{}";
    //log.warm
    public static final String WARM_CAN_GET_CATEGORY = "Не удалось получить категорию с id={}: {}";
    public static final String WARM_CAN_GET_USER = "Не удалось получить пользователя с id={}: {}";
    //exception
    public static final String EXCEPTION_NOT_FOUND_COMPILATION = "Подборка с id=%d не найдена";
    public static final String EXCEPTION_TITLE_CAN_NOT_BLANK = "Заголовок подборки не может быть пустым";
}
