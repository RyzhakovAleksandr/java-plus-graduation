package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.CategoryDto;
import ru.practicum.dto.NewCategoryDto;

import java.util.List;

@Slf4j
@Component
public class CategoryClientFallbackFactory implements FallbackFactory<CategoryClient> {

    @Override
    public CategoryClient create(Throwable cause) {
        return new CategoryClient() {

            @Override
            public CategoryDto addCategory(NewCategoryDto newCategoryDto) {
                log.error(Message.ADD_CATEGORY_SERVICE_NOT_AVAILABLE, cause.getMessage());
                throw new RuntimeException(Message.CATEGORY_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public void deleteCategory(Long catId) {
                log.error(Message.DELETE_CATEGORY_SERVICE_NOT_AVAILABLE, catId);
                throw new RuntimeException(Message.CATEGORY_SERVICE_NOT_AVAILABLE);
            }

            @Override
            public List<CategoryDto> getCategories(Integer from, Integer size) {
                log.warn(Message.GET_CATEGORIES_SERVICE_NOT_AVAILABLE);
                return List.of();
            }

            @Override
            public CategoryDto getCategory(Long catId) {
                log.warn(Message.GET_CATEGORY_SERVICE_NOT_AVAILABLE, catId);
                CategoryDto defaultCategory = new CategoryDto();
                defaultCategory.setId(catId);
                defaultCategory.setName("Unknown Category");
                return defaultCategory;
            }

            @Override
            public CategoryDto updateCategory(Long catId, CategoryDto categoryDto) {
                log.error(Message.UPDATE_CATEGORY_SERVICE_NOT_AVAILABLE, catId);
                throw new RuntimeException(Message.CATEGORY_SERVICE_NOT_AVAILABLE);
            }
        };
    }
}
