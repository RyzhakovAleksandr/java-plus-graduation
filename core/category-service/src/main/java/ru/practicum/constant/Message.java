package ru.practicum.constant;

public class Message {
    //log.info
    public static final String MESSAGE_ADD_CATEGORIES = "POST /admin/categories with request: {}";
    public static final String MESSAGE_DELETE_CATEGORIES = "DELETE /admin/categories/{}";
    public static final String MESSAGE_GET_CATEGORIES = "GET /categories";
    public static final String MESSAGE_GET_CATEGORY = "GET /categories/{}";
    public static final String MESSAGE_UPDATE_CATEGORY = "PATCH /admin/categories/{} with request: {}";
    public static final String LOG_ADDED_CATEGORY = "Добавлена категория: id={}, name={}";
    public static final String LOG_UPDATE_CATEGORY = "Обновлена категория: id={}, name={}";
    public static final String LOG_DELETED_CATEGORY = "Удалена категория: id={}";

    //exception
    public static final String MESSAGE_CATEGORY_NOT_FOUND = "Категория с id=%d не найдена";
    public static final String CAN_NOT_DELETE_CATEGORY = "Нельзя удалить категорию, так как с ней связаны события";
    public static final String TITLE_FOR_CATEGORY_MUST = "Имя категории обязательно для заполнения";
    public static final String TITLE_FOR_CATEGORY_IS_EMPTY = "Имя категории не может быть пустым или состоять только из пробелов";
    public static final String TITLE_IS_TOO_SMALL = "Имя категории должно содержать хотя бы 1 символ";
    public static final String TITLE_IS_TOO_LONG = "Имя категории не может превышать 50 символов";
    public static final String CATEGORY_ALREADY_BE = "Категория с именем %s уже существует";
}
