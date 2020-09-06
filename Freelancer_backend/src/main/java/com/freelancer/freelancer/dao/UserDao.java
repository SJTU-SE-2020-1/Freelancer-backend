package com.freelancer.freelancer.dao;

import com.freelancer.freelancer.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserDao {

    User checkUser(String name, String password);

    User checkDuplicate(String name);

    void addUser(User newUser);

    User findByName(String name);

    User findById(Integer uId);

    void save(User user);

    Page<User> getUsers(String keyword, Pageable pageable);

}
