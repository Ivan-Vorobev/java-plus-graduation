package ru.practicum.explorewithme.feign.category;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.practicum.explorewithme.feign.dto.CategoryDto;

@FeignClient(name = "category-service")
public interface CategoryFeign {
    @GetMapping("/categories/{categoryId}")
    CategoryDto findById(@PathVariable Long categoryId);
}
