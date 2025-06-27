package com.example.task_manager.dtos.request.task;

import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.enums.TaskType;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TaskRequestDto {
    private String id;

    @NotBlank(message = "Title is required")
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters")
    @NotBlank(message = "Description is required")
    private String description;

    @NotNull(message = "Priority is required")
    private TaskPriority priority;

    private TaskStatus status;

    @NotNull(message = "Start date is required")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Date start;

    @NotNull(message = "End date is required")
    @FutureOrPresent(message = "End date must be in the present or future")
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX", timezone = "UTC")
    private Date end;

    @AssertTrue(message = "End date must be after or equal to start date")
    public boolean isValidDateRange() {
        return start == null || end == null || !end.before(start);
    }
}
