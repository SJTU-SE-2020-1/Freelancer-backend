package com.freelancer.freelancer.dao;

import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.UserAvatar;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface UserDao {

    User checkUser(String name, String password);

    User checkDuplicate(String name);

    User addUser(User newUser);

    User findByName(String name);

    User findById(Integer uId);

    void save(User user);

    Page<User> getUsers(String keyword, Pageable pageable);

    Boolean saveAvatar(UserAvatar avatar);

    User changeInfo(String name, String phone, String e_mail, Integer u_id);

    List<Map<String, Object>> getPostedUser(Integer w_id);

}
