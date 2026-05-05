package ru.practicum.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import ru.practicum.constant.Message;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;
import ru.practicum.service.CategoryService;

import java.util.List;

@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping
public class CategoryController {
    private final CategoryService categoryService;

    @PostMapping("/admin/categories")
    public ResponseEntity<CategoryDto> addCategory(@Valid @RequestBody NewCategoryDto newCategoryDto) {
        log.info(Message.MESSAGE_ADD_CATEGORIES, newCategoryDto);
        CategoryDto categoryDto = categoryService.addCategory(newCategoryDto);

        return ResponseEntity.status(HttpStatus.CREATED).body(categoryDto);
    }

    @DeleteMapping("/admin/categories/{catId}")
    public ResponseEntity<Void> deleteCategory(@PathVariable("catId") Long catId) {
        log.info(Message.MESSAGE_DELETE_CATEGORIES, catId);
        categoryService.deleteCategory(catId);

        return ResponseEntity.noContent().build();
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") Integer from,
            @RequestParam(defaultValue = "10") Integer size) {
        log.info(Message.MESSAGE_GET_CATEGORIES);
        List<CategoryDto> categories = categoryService.getCategories(from, size);

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{catId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable("catId") Long catId) {
        log.info(Message.MESSAGE_GET_CATEGORY, catId);
        CategoryDto category = categoryService.getCategoryById(catId);

        return ResponseEntity.ok(category);
    }

    @PatchMapping("/admin/categories/{catId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @PathVariable("catId") Long catId,
            @Valid @RequestBody CategoryDto categoryDto) {
        log.info(Message.MESSAGE_UPDATE_CATEGORY, catId, categoryDto);
        CategoryDto updatedCategory = categoryService.updateCategory(catId, categoryDto);

        return ResponseEntity.ok(updatedCategory);
    }
}
