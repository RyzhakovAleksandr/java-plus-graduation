package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Request;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RequestRepository extends JpaRepository<Request, Long> {
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM requests
            WHERE id = ANY(:ids)
            """)
    List<Request> getRequestsByIds(@Param("ids") Long[] ids);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM requests
            WHERE requester = :userId AND event = :eventId
            """)
    List<Request> getRequestsByUserIdAndEventId(@Param("userId") Long userId, @Param("eventId") Long eventId);

    @Query(nativeQuery = true, value = """
            SELECT COUNT(id)
            FROM requests
            WHERE status = :statusRequest AND event = :eventId
            """)
    Long countByEventIdAndStatus(@Param("eventId") Long eventId, @Param("statusRequest") String statusRequest);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM requests
            WHERE status = :statusRequest AND event = :eventId AND id = ANY(:requestIds)
            """)
    List<Request> findAllByEventIdAndIdInAndStatus(@Param("eventId") Long eventId,
                                                   @Param("requestIds") Long[] requestIds,
                                                   @Param("statusRequest") String statusRequest);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM requests
            WHERE requester = :userId AND id = :requestId
            """)
    Optional<Request> findByIdAndRequesterId(@Param("requestId") Long requestId, @Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM requests
            WHERE requester = :userId
            """)
    List<Request> findAllByRequesterId(@Param("userId") Long userId);

    @Query(nativeQuery = true, value = """
        SELECT r.*
        FROM requests r
        WHERE r.event = :eventId
        """)
    List<Request> findAllByEventId(@Param("eventId") Long eventId);
}
