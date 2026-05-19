package ru.practicum.messeges;

public class Message {
    //info
    public static final String EXISTING_ENTITY = "Существующая запись: userId={}, eventId={}, weight={}, actionType={}";
    public static final String UPDATE_ENTITY = "Обновление записи: userId={}, eventId={}, старый вес={}, новый вес={}";
    public static final String RECEIVED_SIMILARITY = "Получена похожесть: key={}, eventA={}, eventB={}, score={}";
    public static final String GRPC_GET_SIMILAR_EVENT = "gRPC getSimilarEvents: eventId={}, userId={}, maxResults={}";
    public static final String GRPC_RECOMMENDATION_FOR_USER = "gRPC getRecommendationsForUser: userId={}, maxResult={}";
    public static final String GRPC_INTERACTION = "gRPC getInteractionsCount: {} events";
    public static final String SEND_SIMILARITY_EVENT = "Отправлено {} похожих событий для eventId={}";
    public static final String SEND_RECOMMENDATION_FOR_USER = "Отправлено {} рекомендаций для userId={}";
    public static final String TIME_QUERY = "Время выполнения запроса: {} мс для {} событий";
    public static final String SEND_INTERACTION = "Отправлено количество взаимодействий для {} событий";
    //debug
    public static final String SKIPPING_UPDATE_ENTITY = "Пропуск обновления: новый вес {} <= старый вес {}";
    public static final String SAVED_SIMILARITY = "Сохранена похожесть: eventA={}, eventB={}, score={}";
    public static final String FIND_SIMILAR_EVENTS = "Поиск похожих событий по: eventId={}, userId={}";
    public static final String GET_RECOMMENDATION_FOR_USER = "Получение персональных рекомендаций для userId={}";
    public static final String USER_DONT_HAVE_ACTION = "У пользователя нет последних действий";
    //error
    public static final String ERROR_GRPC_GET_SIMILARITY = "Ошибка в gRPC getSimilarEvents";
    public static final String ERROR_GRPC_RECOMMENDATION = "Ошибка в gRPC getRecommendationsForUser";
    public static final String ERROR_GRPC_INTERACTION = "Ошибка в gRPC getInteractionsCount";
}
