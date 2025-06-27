package com.example.task_manager.repositories;

import com.example.task_manager.entities.Task;
import com.example.task_manager.enums.TaskType;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TaskRepository extends MongoRepository<Task, String> {
    boolean existsByIdAndType(String id, TaskType type);
    List<Task> findByTeamIdAndType(String teamId, TaskType type);
    List<Task> findByAssignedUsersIdAndType(String id, TaskType type);
    void deleteByAssignedUsersIdAndType(String userId, TaskType type);
    void deleteByIdAndTeamIdAndType(String teamId, String taskId, TaskType type);
    void deleteByTeamId(String teamId);
}
