package main.java.ru.practicum.service;

import ru.practicum.openapi.model.CategoryDto;
import ru.practicum.openapi.model.NewCategoryDto;

import java.util.List;

public interface CategoryService {
    CategoryDto addCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    List<CategoryDto> getCategories(Integer from, Integer size);

    CategoryDto getCategoryById(Long categoryId);

    void deleteCategory(Long categoryId);
}
