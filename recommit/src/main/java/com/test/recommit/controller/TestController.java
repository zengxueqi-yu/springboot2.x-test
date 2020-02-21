package com.test.recommit.controller;

import com.test.recommit.annotations.RequestLimit;
import com.test.recommit.common.ApiResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class TestController {

    @RequestLimit(reqSec = 5, reqSecCount = 2)
    @GetMapping("/test")
    public ApiResult<String> test(){
        return ApiResult.ok("测试接口防刷与限流");
    }

}
