package ru.practicum.explorewithme.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.dto.NewCategoryDto;
import ru.practicum.explorewithme.feign.dto.EventDto;
import ru.practicum.explorewithme.feign.event.EventFeign;
import ru.practicum.explorewithme.mapper.CategoryMapper;
import ru.practicum.explorewithme.model.Category;
import ru.practicum.explorewithme.repository.CategoryRepository;
import ru.practicum.explorewithme.exception.DataAlreadyExistException;
import ru.practicum.explorewithme.exception.NotFoundException;
import ru.practicum.explorewithme.exception.RelatedDataDeleteException;

import java.util.Collection;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class CategoryServiceImpl implements CategoryService {
    private final EventFeign eventFeign;
    private final CategoryRepository categoryRepository;
    private final CategoryMapper categoryMapper;

    @Override
    public CategoryDto createCategory(NewCategoryDto newCategoryDto) {
        Category newCategory = categoryMapper.toModel(newCategoryDto);
        try {
            Category createdCategory = categoryRepository.save(newCategory);
            return categoryMapper.toDto(createdCategory);
        } catch (DataIntegrityViolationException e) {
            final String error = String.format("The category with name=%s already exists in the database.",
                    newCategory.getName());
            log.warn(error);
            throw new DataAlreadyExistException(error);
        }
    }

    @Override
    public CategoryDto updateCategory(Long categoryId, CategoryDto categoryDto) {
        Category currentCategory = getCategoryById(categoryId);
        currentCategory.setName(categoryDto.getName());
        try {
            Category updatedCategory = categoryRepository.save(currentCategory);
            return categoryMapper.toDto(updatedCategory);
        } catch (DataIntegrityViolationException e) {
            final String error = String.format("The category with name=%s already exists in the database.",
                    categoryDto.getName());
            log.warn(error);
            throw new DataAlreadyExistException(error);
        }
    }

    @Override
    public void deleteCategory(Long categoryId) {
        List<EventDto> events = eventFeign.getEventsByCategories(List.of(categoryId));
        if (events == null || events.isEmpty()) {
            categoryRepository.deleteById(categoryId);
        } else {
            final String error = String.format("Attempt to delete a category id=%d related to event.", categoryId);
            log.warn(error);
            throw new RelatedDataDeleteException(error);
        }
    }

    @Override
    public CategoryDto getCategory(Long categoryId) {
        Category currentCategory = getCategoryById(categoryId);
        return categoryMapper.toDto(currentCategory);
    }

    @Override
    public Collection<CategoryDto> getCategories(Pageable pageable) {
        return categoryRepository.findAll(pageable).stream()
                .map(categoryMapper::toDto)
                .toList();
    }

    public Category getCategoryById(Long categoryId) {
        return categoryRepository.findById(categoryId)
                .orElseThrow(() -> new NotFoundException(String.format("Category with id=%d not found.", categoryId)));
    }
}
