package ru.practicum.messages;

public class Message {
    //info
    public static final String SEND_SIMILARITY_TO_KAFKA = "Отправка похожести в Kafka: topic={}, key={}, eventA={}, eventB={}, score={}";
    public static final String SIMILARITY_SUCCESS = "Похожесть успешно отправлена: offset={}, partition={}";
    public static final String RECEIVED_FROM_KAFKA = "Получено из Kafka: userId={}, eventId={}, actionType={}";
    public static final String NEW_WEIGHT_FOR_ACTION = "Новый вес для действия {} равен {}";
    //debug
    public static final String PROCESS_USER_ACTION = "Обработка действия пользователя: userId={}, eventId={}, newWeight={}";
    public static final String WEIGHT_NOT_UPDATE = "Вес не увеличен для пользователя={}, события={}, старый={}, новый={}, пропуск";
    public static final String DELTA_FOR_EVENT = "Дельта для события {}: {}";
    public static final String WEIGHT_UPDATE = "Обновлена сумма весов для события {}: {} -> {}";
    public static final String SUM_WEIGHT_FOR_EVENT = "Сумма весов для события {} равна 0, пропуск";
    public static final String SUM_WEIGHT_FOR_EVENTS = "Сумма весов для события {} равна 0, пропуск пары ({}, {})";
    public static final String USER_GET_EVENT = "Пользователь {} взаимодействовал с событиями: {}";
    public static final String USER_DONT_HAVE_WEIGHT = "У пользователя {} нет веса для события {}, пропуск пары ({}, {})";
    public static final String PAIR_EVENTS = "Пара ({}, {}): oldMin={}, newMin={}, deltaMin={}";
    public static final String SIMILARITY_CALCULATED = "Похожесть рассчитана: eventA={}, eventB={}, score={}";
    public static final String SIMILARITY_RECALCULATED = "Пересчитано {} похожестей для eventA={} и userId={}";
    //warn
    public static final String SIMILARITY_IS_NAN = "Похожесть равна NaN для пары ({}, {}), установка в 0";
    //error
    public static final String FAILED_SEND_SIMILARITY = "Не удалось отправить похожесть для key={}: {}";
    public static final String ERROR_PROCESS_ACTION = "Ошибка при обработке действия пользователя";
}
