package com.freelancer.freelancer.repository;

import com.freelancer.freelancer.entity.User;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.repository.query.Modifying;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface UserRepository extends JpaRepository<User, Integer> {

    @Query(value = "from User where name = :name and password = :password")
    User checkUser(@Param("name") String name, @Param("password") String password);

    @Query(value = "from User where name = :name")
    User checkDuplicate(@Param("name") String name);

    @Query(value = "from User where name = :name")
    User findByName(@Param("name") String name);

    @Query(value = "select u from User u where u.name like %?1% ")
    Page<User> getUsers(String keyword, Pageable pageable);

    @Modifying
    @Transactional
    @Query(value = "update user set name = :name,phone=:phone,e_mail=:e_mail  where u_id = :u_id", nativeQuery = true)
    void updateInfoByid(@Param("name") String name, @Param("phone") String phone, @Param("e_mail") String e_mail,
            @Param("u_id") Integer u_id);

    @Query(value = "select * from (select *  from propose_work where w_id=?1 )as p natural join `user`", nativeQuery = true)
    List<Map<String, Object>> getPostUsers(Integer w_id);

}
