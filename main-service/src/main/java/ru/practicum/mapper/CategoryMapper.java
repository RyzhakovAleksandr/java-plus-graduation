package main.java.ru.practicum.mapper;

import main.java.ru.practicum.persistence.entity.Category;

import org.mapstruct.Mapper;

import ru.practicum.openapi.model.NewCategoryDto;
import ru.practicum.openapi.model.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    public Category toCategory(NewCategoryDto newCategoryDto);

    public CategoryDto toCategoryDto(Category category);
}
