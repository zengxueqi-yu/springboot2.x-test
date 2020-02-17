package com.test.jpa.service;

import com.test.jpa.entity.Userinfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserInfoService extends JpaRepository<Userinfo,Long> {

    List<Userinfo> findByUsername(String username);

}
