package ru.practicum.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.dto.CategoryDto;

@FeignClient(name = "category-service", fallbackFactory = CategoryClientFallbackFactory.class)
public interface CategoryClient {

    @GetMapping("/categories/{catId}")
    CategoryDto getCategory(@PathVariable("catId") Long catId);

}
