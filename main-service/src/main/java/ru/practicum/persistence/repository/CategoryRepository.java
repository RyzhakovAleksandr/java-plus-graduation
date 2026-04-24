package main.java.ru.practicum.persistence.repository;

import main.java.ru.practicum.persistence.entity.Category;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query(nativeQuery = true, value = """
            SELECT *
            FROM categories
            WHERE categories.id = ANY(:ids)
            """)
    List<Category> getCategoriesByIds(@Param("ids") Long[] ids);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);
}
