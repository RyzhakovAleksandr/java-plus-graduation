package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

@FeignClient(name = "category-service", fallbackFactory = CategoryClientFallbackFactory.class)
public interface CategoryClient {
    @PostMapping("/admin/categories")
    CategoryDto addCategory(@RequestBody NewCategoryDto newCategoryDto);

    @DeleteMapping("/admin/categories/{catId}")
    void deleteCategory(@PathVariable("catId") Long catId);

    @GetMapping("/categories")
    List<CategoryDto> getCategories(@RequestParam("from") Integer from, @RequestParam("size") Integer size);

    @GetMapping("/categories/{catId}")
    CategoryDto getCategory(@PathVariable("catId") Long catId);

    @PatchMapping("/admin/categories/{catId}")
    CategoryDto updateCategory(@PathVariable("catId") Long catId, @RequestBody CategoryDto categoryDto);
}
