package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> userIds, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    @Query(nativeQuery = true, value = """
            SELECT *
            FROM users
            WHERE id = ANY(:ids)
            """)
    List<User> getInitiatorByCompilationIds(@Param("ids") Long[] ids);

    boolean existsByEmail(String email);
}
