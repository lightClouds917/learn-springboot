package com.java4all.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Author: yunqing
 * Date: 2018/7/10
 * Description:登陆
 */
@RestController
@RequestMapping("user")
public class UserController {

    @RequestMapping(value = "login",method = RequestMethod.GET)
    public String login(String userName){
        return "登陆成功！";
    }

    @RequestMapping(value = "register",method = RequestMethod.GET)
    public String register(){
        try {
            Thread.sleep(3000);
        }catch (InterruptedException ex){
            ex.printStackTrace();
        }
        return "注册成功！";
    }
}
