package ru.practicum.repository;

import ru.practicum.model.User;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Long> {
    List<User> findAllByIdIn(List<Long> userIds, Pageable pageable);

    Page<User> findAll(Pageable pageable);

    boolean existsByEmail(String email);
}
