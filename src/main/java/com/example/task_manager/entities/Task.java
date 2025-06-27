package com.example.task_manager.entities;

import com.example.task_manager.enums.TaskPriority;
import com.example.task_manager.enums.TaskStatus;
import com.example.task_manager.enums.TaskType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Task")
public class Task {
    @Id
    private String id;
    private String title;
    private String description;
    private TaskType type;
    private TaskPriority priority;
    private TaskStatus status;
    private List<String> assignedUsersId;
    private String teamId;
    private Date start;
    private Date end;
    private Date createdAt;
    private Date modifiedAt;
}
