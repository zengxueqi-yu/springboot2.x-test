package com.test.jpa.controller;

import com.test.jpa.entity.Userinfo;
import com.test.jpa.service.UserInfoService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.List;

@RestController
@RequestMapping("/api")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    @GetMapping("/getUserInfos")
    public List<Userinfo> getUserInfos(){
        return userInfoService.findByUsername("lisheng");
    }

}
