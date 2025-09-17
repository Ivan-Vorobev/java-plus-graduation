package ru.practicum.explorewithme.dto;

import lombok.Data;
import ru.practicum.explorewithme.model.RequestStatus;

import java.util.List;

@Data
public class ChangeRequestStatusDto {
    List<Long> requestIds;
    RequestStatus status;
}
