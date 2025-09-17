package ru.practicum.explorewithme.service;

import org.springframework.data.domain.Pageable;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.model.Category;

import java.util.Collection;

public interface CategoryService {
    CategoryDto createCategory(NewCategoryDto newCategoryDto);

    CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto);

    void deleteCategory(Long categoryId);

    CategoryDto getCategory(Long categoryId);

    Collection<CategoryDto> getCategories(Pageable pageable);

    Category getCategoryById(Long categoryId);
}
