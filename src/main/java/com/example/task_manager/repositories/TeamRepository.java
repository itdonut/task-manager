package com.example.task_manager.repositories;

import com.example.task_manager.entities.Team;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TeamRepository extends MongoRepository<Team, String> {
    public List<Team> findByOwnerId(String ownerId);
    public List<Team> findByMembersId(String userId);
}
