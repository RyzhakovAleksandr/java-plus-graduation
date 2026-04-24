package main.java.ru.practicum.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import dto.ViewStats;

import main.java.ru.practicum.model.EndpointHit;

import java.time.LocalDateTime;
import java.util.List;

public interface StatsRepository extends JpaRepository<EndpointHit, Long> {
    @Query(nativeQuery = true, value = """
            SELECT app, uri,
            CASE WHEN :unique = true THEN COUNT(DISTINCT ip) ELSE COUNT(ip) END AS hits
            FROM hits
            WHERE hit_time BETWEEN :start AND :end
            AND uri = ANY(:uris)
            GROUP BY app, uri
            ORDER BY hits DESC""")
    List<ViewStats> getStats(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("uris") String[] uris,
            @Param("unique") Boolean unique);

    @Query(nativeQuery = true, value = """
            SELECT app, uri,
            CASE WHEN :unique = true THEN COUNT(DISTINCT ip) ELSE COUNT(ip) END AS hits
            FROM hits
            WHERE hit_time BETWEEN :start AND :end
            GROUP BY app, uri
            ORDER BY hits DESC""")
    List<ViewStats> getStatsWithoutUris(
            @Param("start") LocalDateTime start,
            @Param("end") LocalDateTime end,
            @Param("unique") Boolean unique);
}
