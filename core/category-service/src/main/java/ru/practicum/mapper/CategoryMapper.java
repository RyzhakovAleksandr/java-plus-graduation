package ru.practicum.mapper;

import ru.practicum.model.Category;

import org.mapstruct.Mapper;

import ru.practicum.dto.NewCategoryDto;
import ru.practicum.dto.CategoryDto;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    public Category toCategory(NewCategoryDto newCategoryDto);

    public CategoryDto toCategoryDto(Category category);
}
