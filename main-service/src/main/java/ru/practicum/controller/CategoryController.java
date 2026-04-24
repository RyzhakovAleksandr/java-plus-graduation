package main.java.ru.practicum.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import main.java.ru.practicum.constant.Messages;
import main.java.ru.practicum.service.CategoryService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.openapi.api.CategoryApi;
import ru.practicum.openapi.model.CategoryDto;
import ru.practicum.openapi.model.NewCategoryDto;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
public class CategoryController implements CategoryApi {
    private final CategoryService categoryService;

    @Override
    public ResponseEntity<CategoryDto> addCategory(NewCategoryDto newCategoryDto) {
        log.info(Messages.MESSAGE_ADD_CATEGORIES, newCategoryDto);
        CategoryDto categoryDto = categoryService.addCategory(newCategoryDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @Override
    public ResponseEntity<Void> deleteCategory(Long catId) {
        log.info(Messages.MESSAGE_DELETE_CATEGORIES, catId);
        categoryService.deleteCategory(catId);

        return ResponseEntity.noContent().build();
    }

    @Override
    public ResponseEntity<List<CategoryDto>> getCategories(Integer from, Integer size) {
        log.info(Messages.MESSAGE_GET_CATEGORIES);
        List<CategoryDto> categories = categoryService.getCategories(from, size);

        return ResponseEntity.ok(categories);
    }

    @Override
    public ResponseEntity<CategoryDto> getCategory(Long catId) {
        log.info(Messages.MESSAGE_GET_CATEGORY, catId);
        CategoryDto category = categoryService.getCategoryById(catId);

        return ResponseEntity.ok(category);
    }

    @Override
    public ResponseEntity<CategoryDto> updateCategory(Long catId, CategoryDto categoryDto) {
        log.info(Messages.MESSAGE_UPDATE_CATEGORY, catId, categoryDto);
        CategoryDto updatedCategory = categoryService.updateCategory(catId, categoryDto);

        return ResponseEntity.ok(updatedCategory);
    }
}
