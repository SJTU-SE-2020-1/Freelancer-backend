package com.freelancer.freelancer.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.Data;

@Data
@Document(collection = "useravatar")
public class UserAvatar {

    @Id
    private int id;
    private String avatar;

    public UserAvatar(int id, String avatar) {
        this.id = id;
        this.avatar = avatar;
    }

}