package com.freelancer.freelancer.daoimpl;

import com.alibaba.fastjson.JSONObject;
import com.freelancer.freelancer.dao.UserDao;
import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.UserAvatar;
import com.freelancer.freelancer.entity.WorkEnclosure;
import com.freelancer.freelancer.repository.UserAvatarRepository;
import com.freelancer.freelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.transaction.Transactional;

@Repository
public class UserDaoImpl implements UserDao {

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserAvatarRepository userAvatarRepository;

    @Override
    public User checkUser(String name, String password) {
        return userRepository.checkUser(name, password);
    }

    @Override
    public User checkDuplicate(String name) {
        return userRepository.checkDuplicate(name);
    }

    @Override
    public User addUser(User newUser) {
        // UserAvatar userAvatar = new UserAvatar(newUser.getU_id(),
        // newUser.getAvatar());
        // userAvatarRepository.save(userAvatar);
        return userRepository.save(newUser);
    }

    @Override
    public User findByName(String name) {
        User user = userRepository.findByName(name);
        Optional<UserAvatar> userAvatar = userAvatarRepository.findById(user.getU_id());
        if (userAvatar.isPresent()) {
            user.setAvatar(userAvatar.get().getAvatar());
        } else {
            user.setAvatar(null);
        }
        return user;
    }

    @Override
    public User findById(Integer uId) {
        User user = userRepository.findById(uId).get();
        Optional<UserAvatar> userAvatar = userAvatarRepository.findById(user.getU_id());
        if (userAvatar.isPresent()) {
            user.setAvatar(userAvatar.get().getAvatar());
        } else {
            user.setAvatar(null);
        }
        return user;
    }

    @Transactional
    @Override
    public void save(User user) {
        userRepository.save(user);
    }

    @Override
    public Page<User> getUsers(String keyword, Pageable pageable) {
        return userRepository.getUsers(keyword, pageable);
    }

    @Override
    public Boolean saveAvatar(UserAvatar avatar) {
        userAvatarRepository.save(avatar);
        return true;
    }

    @Override
    public User changeInfo(String name, String phone, String e_mail, Integer u_id) {
        userRepository.updateInfoByid(name, phone, e_mail, u_id);
        User aUser = userRepository.findById(u_id).get();
        return aUser;
    }

    @Override
    public List<Map<String, Object>> getPostedUser(Integer w_id) {
        List<Map<String, Object>> users = userRepository.getPostUsers(w_id);
        Optional<UserAvatar> userAvatar;
        List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
        for (Map<String, Object> user : users) {
            userAvatar = userAvatarRepository.findById(Integer.parseInt(user.get("u_id").toString()));
            Map<String, Object> newuser = new HashMap<>(user);
            if (userAvatar.isPresent()) {
                newuser.put("avatar", userAvatar.get().getAvatar());

            } else {
                newuser.put("avatar", "");
            }
            result.add(newuser);
        }
        return result;

    }
}
