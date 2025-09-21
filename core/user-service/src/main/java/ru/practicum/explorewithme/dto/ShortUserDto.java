package ru.practicum.explorewithme.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ShortUserDto {
    private Long id;
    private String name;
}
