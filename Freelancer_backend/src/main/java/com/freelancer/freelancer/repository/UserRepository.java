package com.freelancer.freelancer.repository;

import com.freelancer.freelancer.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "from User where name = :name and password = :password")
    User checkUser(@Param("name") String name, @Param("password") String password);

    @Query(value = "from User where name = :name")
    User checkDuplicate(@Param("name") String name);

    @Query(value = "from User where name = :name")
    User findByName(@Param("name") String name);

    @Query(value = "select u from User u where u.name like %?1% ")
    Page<User> getUsers(String keyword, Pageable pageable);

}
