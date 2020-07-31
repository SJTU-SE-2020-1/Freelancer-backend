package com.freelancer.freelancer.daoimpl;

import com.freelancer.freelancer.dao.UserDao;
import com.freelancer.freelancer.entity.User;
import com.freelancer.freelancer.entity.UserAvatar;
import com.freelancer.freelancer.entity.WorkEnclosure;
import com.freelancer.freelancer.repository.UserAvatarRepository;
import com.freelancer.freelancer.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

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
    public void addUser(User newUser) {
        UserAvatar userAvatar = new UserAvatar(newUser.getU_id(), newUser.getAvatar());
        userAvatarRepository.save(userAvatar);
        userRepository.save(newUser);
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

}
