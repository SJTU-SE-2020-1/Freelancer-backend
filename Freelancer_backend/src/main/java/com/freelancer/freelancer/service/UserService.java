package com.freelancer.freelancer.service;

import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.UserAvatar;
import com.freelancer.freelancer.utils.msgutils.Msg;
import com.freelancer.freelancer.utils.msgutils.MsgCode;
import com.freelancer.freelancer.utils.msgutils.MsgUtil;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Map;

public interface UserService {

    User checkUser(String name, String password);

    User checkDuplicate(String name);

    User addUser(User newUser);

    User findByName(String name);

    User findById(Integer uId);

    Msg login(String name, String password);

    boolean changeUserStatus(Integer u_id, Integer status);

    Page<User> getUsers(String keyword, Pageable pageable);

    boolean changeUserType(Integer u_id, Integer type);

    Boolean saveAvatar(UserAvatar avatar);

    User changeInfo(String name, String phone, String e_mail, Integer u_id);

    List<Map<String, Object>> getPostedUser(Integer w_id);

}