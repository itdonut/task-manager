package com.example.task_manager.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "Team")
public class Team {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private Date createdAt;
    private Date modifiedAt;
}
