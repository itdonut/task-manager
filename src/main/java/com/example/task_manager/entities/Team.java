package com.example.task_manager.entities;

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
@Document(collection = "Team")
public class Team {
    @Id
    private String id;
    private String name;
    private String ownerId;
    private List<String> membersId;
    private Date createdAt;
    private Date modifiedAt;
}
