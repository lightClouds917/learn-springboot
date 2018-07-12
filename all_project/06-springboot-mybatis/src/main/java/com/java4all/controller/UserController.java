package com.java4all.controller;

import com.java4all.entity.User;
import com.java4all.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Author: yunqing
 * Date: 2018/7/12
 * Description:第06课：Spring boot整合Mybatis
 */
@RestController
@RequestMapping("user")
public class UserController {

    @Autowired
    private UserService userService;

    @RequestMapping(value = "getUserList",method = RequestMethod.GET)
    public Object getUserList(){
        List<User> list = userService.getUserList();
        return list;
    }
}
