package ru.practicum.explorewithme.controller;

import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.explorewithme.dto.CategoryDto;
import ru.practicum.explorewithme.service.CategoryService;

import java.util.Collection;

@RestController
@RequestMapping(path = "/categories")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class PublicCategoriesController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<Collection<CategoryDto>> getCategories(
            @RequestParam(defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(defaultValue = "10") @Positive Integer size) {
        Pageable pageable = PageRequest.of(from / size, size, Sort.by("id").ascending());
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getCategories(pageable));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable Long categoryId) {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(categoryService.getCategory(categoryId));
    }
}
