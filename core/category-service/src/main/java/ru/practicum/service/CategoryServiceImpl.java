package ru.practicum.service;

import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.client.EventClient;
import ru.practicum.constant.Message;
import ru.practicum.exception.ForbiddenException;
import ru.practicum.exception.NotFoundException;
import ru.practicum.exception.ValidationException;
import ru.practicum.mapper.CategoryMapper;
import ru.practicum.model.Category;
import ru.practicum.repository.CategoryRepository;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;


import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    private final EventClient eventClient;

    @Override
    @Transactional
    public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
        validateCategoryName(newCategoryDto.getName());
        validateCategoryNotExists(newCategoryDto.getName());

        Category category = categoryMapper.toCategory(newCategoryDto);
        category = categoryRepository.save(category);

        log.info(Message.LOG_ADDED_CATEGORY, category.getId(), category.getName());

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category category = getCategory(categoryId);
        validateCategoryName(categoryDto.getName());

        if (!category.getName().equals(categoryDto.getName())) {
            validateCategoryNotExists(categoryDto.getName());
        }

        category.setName(categoryDto.getName());
        category = categoryRepository.save(category);

        log.info(Message.LOG_UPDATE_CATEGORY, category.getId(), category.getName());

        return categoryMapper.toCategoryDto(category);
    }

    @Override
    @Transactional(readOnly = true)
    public List<CategoryDto> getCategories(Integer from, Integer size) {
        return categoryRepository.findAll(PageRequest.of(from / size, size))
                .stream()
                .map(categoryMapper::toCategoryDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public CategoryDto getCategoryById(Long categoryId) {
        return categoryMapper.toCategoryDto(getCategory(categoryId));
    }

    @Override
    @Transactional
    public void deleteCategory(Long categoryId) {
        Category category = getCategory(categoryId);

        boolean hasEvents = eventClient.hasEventsByCategory(categoryId);
        if (hasEvents) {
            throw new ForbiddenException("Нельзя удалить категорию, так как с ней связаны события");
        }
        categoryRepository.delete(category);
        log.info(Message.LOG_DELETED_CATEGORY, categoryId);
    }

    private Category getCategory(Long categoryId) {
        return categoryRepository.findById(categoryId).orElseThrow(() ->
                new NotFoundException(String.format(Message.MESSAGE_CATEGORY_NOT_FOUND, categoryId)));
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

    private void validateCategoryNotExists(String name) {
        if (categoryRepository.existsByName(name)) {
            throw new ForbiddenException("Категория с именем '" + name + "' уже существует");
        }
    }
}
