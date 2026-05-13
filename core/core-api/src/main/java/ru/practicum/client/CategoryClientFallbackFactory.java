package ru.practicum.client;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;
import ru.practicum.constant.Message;
import ru.practicum.dto.CategoryDto;

@Slf4j
@Component
public class CategoryClientFallbackFactory implements FallbackFactory<CategoryClient> {

    @Override
    public CategoryClient create(Throwable cause) {
        return new CategoryClient() {

            @Override
            public CategoryDto getCategory(Long catId) {
                log.warn(Message.GET_CATEGORY_SERVICE_NOT_AVAILABLE, catId);
                CategoryDto defaultCategory = new CategoryDto();
                defaultCategory.setId(catId);
                defaultCategory.setName("Unknown Category");
                return defaultCategory;
            }
        };
    }
}
