package ru.practicum.messages;

public class Message {
    //info
    public static final String LOG_COLLECT_USER_CATION = "Получено действие пользователя: userId={}, eventId={}, actionType={}";
    public static final String LOG_USER_ACTION_OK = "Действие пользователя успешно обработано";
    public static final String LOG_SEND_KAFKA = "Отправка в Kafka: topic={}, key={}, eventId={}, actionType={}";
    //debug
    public static final String MESSAGE_SEND = "Сообщение отправлено: partition={}, offset={}";
    //error
    public static final String ERROR_USER_ACTION = "Ошибка при обработке действия пользователя";
    public static final String ERROR_SEND = "Failed to send message ";
    //exception
    public static final String UNKNOW_ACTION = "Неизвестный тип действия: %s";
}
