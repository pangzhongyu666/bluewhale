package com.seecoder.BlueWhale.repository;

import com.seecoder.BlueWhale.po.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;


public interface UserRepository extends JpaRepository<User, Integer> {
    User findByPhone(String phone);
    User findByPhoneAndPassword(String phone, String password);

    // 在UserRepository中添加
    @Query("SELECT p.privilegeMethod FROM User u " +
            "JOIN Role r ON u.role = r.role " +
            "JOIN r.privileges p " +
            "WHERE u.id = :userId")
    List<String> findCurrUserPrivilegesById(@Param("userId") Integer userId);
}
