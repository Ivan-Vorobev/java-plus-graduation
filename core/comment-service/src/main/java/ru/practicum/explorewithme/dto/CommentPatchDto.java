package ru.practicum.explorewithme.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import ru.practicum.explorewithme.model.CommentStatus;

@Data
public class CommentPatchDto {

    @NotBlank
    private CommentStatus status;
}
