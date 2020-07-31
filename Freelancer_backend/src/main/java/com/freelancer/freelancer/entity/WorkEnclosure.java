package com.freelancer.freelancer.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "workenclosure")
public class WorkEnclosure {

    @Id
    private int id;
    private String description;

    public WorkEnclosure(int id, String description) {
        this.id = id;
        this.description = description;
    }

}