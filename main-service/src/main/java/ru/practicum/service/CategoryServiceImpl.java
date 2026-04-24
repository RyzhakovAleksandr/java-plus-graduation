package main.java.ru.practicum.service;

import lombok.RequiredArgsConstructor;

import main.java.ru.practicum.constant.Exceptions;
import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.exception.ForbiddenException;
import main.java.ru.practicum.exception.ValidationException;
import main.java.ru.practicum.mapper.CategoryMapper;
import main.java.ru.practicum.persistence.entity.Category;
import main.java.ru.practicum.persistence.repository.CategoryRepository;

import main.java.ru.practicum.persistence.repository.EventRepository;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.openapi.model.CategoryDto;
import ru.practicum.openapi.model.NewCategoryDto;

import main.java.ru.practicum.exception.NotFoundException;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final EventRepository eventRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.getName());

        if (categoryRepository.existsByName(newCategoryDto.getName())) {
            throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_CATEGORY, newCategoryDto.getName()));
        }
        return categoryMapper.toCategoryDto(categoryRepository.save(categoryMapper.toCategory(newCategoryDto)));
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = getCategory(categoryId);
        validateCategoryName(categoryDto.getName());
        if (!category.getName().equals(categoryDto.getName())) {
            boolean nameExists = categoryRepository.existsByNameAndIdNot(categoryDto.getName(), categoryId);
            if (nameExists) {
                throw new ForbiddenException(String.format(Exceptions.EXCEPTION_CONFLICT_CATEGORY, categoryDto.getName()));
            }
        }

        category.setName(categoryDto.getName());
        return categoryMapper.toCategoryDto(categoryRepository.save(category));
    }

    @Override
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size)).stream()
                .map(categoryMapper::toCategoryDto).collect(Collectors.toList());
    }

    @Override
    public CategoryDto getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    public void deleteCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId));
        }

        if (eventRepository.existsByCategoryId(categoryId)) {
            throw new ForbiddenException(Exceptions.EXCEPTION_CANT_DELETE_CATEGORY);
        }

        categoryRepository.deleteById(categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(Messages.MESSAGE_CATEGORY_NOT_FOUND, categoryId)));
    }

    private void validateCategoryName(String name) {
        if (name == null) {
            throw new ValidationException("Имя категории обязательно для заполнения");
        }
        String trimmed = name.trim();
        if (trimmed.isEmpty()) {
            throw new ValidationException("Имя категории не может быть пустым или состоять только из пробелов");
        }
        if (trimmed.length() < 1) {
            throw new ValidationException("Имя категории должно содержать хотя бы 1 символ");
        }
        if (trimmed.length() > 50) {
            throw new ValidationException("Имя категории не может превышать 50 символов");
        }
    }
}
