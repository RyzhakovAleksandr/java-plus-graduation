package ru.practicum.repository;

import ru.practicum.model.Event;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<Event, Long>, JpaSpecificationExecutor<Event> {
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM events
            WHERE initiator = :userId AND id = :eventId
            """)
    Optional<Event> getEventByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM events
            WHERE initiator = :userId
            LIMIT :size
            OFFSET :from
            """)
    List<Event> getEventsUser(@Param("userId") Long userId, @Param("from") Integer from, @Param("size") Integer size);

    @Query("SELECT COUNT(e) > 0 FROM Event e WHERE e.category = :categoryId")
    boolean existsByCategoryId(@Param("categoryId") Long categoryId);
}
