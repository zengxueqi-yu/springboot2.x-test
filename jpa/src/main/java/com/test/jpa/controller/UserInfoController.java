package com.test.jpa.controller;

import com.test.jpa.entity.Userinfo;
import com.test.jpa.service.UserInfoService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

/**
 * 用户控制器信息
 * @author zengxueqi
 * @since 2020/3/24
 */
@RestController
@RequestMapping("/api")
public class UserInfoController {

    @Resource
    private UserInfoService userInfoService;

    /**
     * 获取用户信息
     * @param userinfo
     * @return java.util.List<com.test.jpa.entity.Userinfo>
     * @author zengxueqi
     * @since 2020/3/24
     */
    @PostMapping("/getUserInfos")
    public List<Userinfo> getUserInfos(@RequestBody Userinfo userinfo){
        return userInfoService.findByUsername("lisheng");
    }

}
