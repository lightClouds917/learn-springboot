package com.java4all.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * author:lightClouds917
 * date:2018/7/3
 * description:
 */
@RestController//@Controller和@ResponseBody的组合，这样返回的数据自动转为json格式
@RequestMapping(value = "login")
public class LoginController {

    /**登陆测试*/
    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String login(){
        return "欢迎进入Spring Boot的世界";
    }
}
