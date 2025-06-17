package com.example.task_manager.repositories;

import com.example.task_manager.entities.User;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends MongoRepository<User, String> {
    Optional<User> getUserById(String id);
    Optional<User> getUserByUsername(String username);
}
