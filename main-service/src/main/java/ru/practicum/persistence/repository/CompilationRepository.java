package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Compilation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CompilationRepository extends JpaRepository<Compilation, Long> {
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM compilations
            WHERE (:pinned IS NULL OR pinned = :pinned)
            ORDER BY id
            OFFSET CAST(:from AS INTEGER)
            LIMIT CAST(:size AS INTEGER)
            """)
    List<Compilation> getCompilations(@Param("pinned") Boolean pinned,
                                      @Param("from") Integer from,
                                      @Param("size") Integer size);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM compilations
            WHERE id = :compId
            """)
    Optional<Compilation> getCompilation(@Param("compId") Long compId);
}
