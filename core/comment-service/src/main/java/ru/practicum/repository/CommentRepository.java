package ru.practicum.repository;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.practicum.model.Comment;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment, Long> {
    List<Comment> findAllByAuthorId(Long userId, Pageable pageable);

    List<Comment> findAllByEventId(Long eventId, Pageable pageable);

    @Query("SELECT COUNT(c) > 0 FROM Comment c WHERE c.id = :commentId AND c.authorId = :authorId")
    boolean existsByIdAndAuthorId(@Param("commentId") Long commentId, @Param("authorId") Long authorId);
}
