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
}
