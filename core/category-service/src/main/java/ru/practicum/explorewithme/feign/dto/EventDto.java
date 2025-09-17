package ru.practicum.explorewithme.feign.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Builder;
import lombok.Data;
import ru.practicum.explorewithme.config.CustomLocalDateTimeDeserializer;
import ru.practicum.explorewithme.config.CustomLocalDateTimeSerializer;
import ru.practicum.explorewithme.dto.CategoryDto;

import java.time.LocalDateTime;

@Data
@Builder
public class EventDto {
    private Long id;
    private String title;
    private String annotation;
    private String description;
    private CategoryDto category;
    private ShortUserDto initiator;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) // Для входящих данных
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)     // Для исходящих данных
    private LocalDateTime eventDate;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) // Для входящих данных
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)     // Для исходящих данных
    private LocalDateTime createdOn;
    @JsonDeserialize(using = CustomLocalDateTimeDeserializer.class) // Для входящих данных
    @JsonSerialize(using = CustomLocalDateTimeSerializer.class)     // Для исходящих данных
    private LocalDateTime publishedOn;
    private LocationDto location;
    private Integer participantLimit;
    private Boolean paid;
    private Boolean requestModeration;
    private String state;
    private Integer confirmedRequests;
    private Long views;
}
