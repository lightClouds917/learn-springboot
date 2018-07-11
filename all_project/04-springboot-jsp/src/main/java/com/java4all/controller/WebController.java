package com.java4all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * Author: momo
 * Date: 2018/7/11
 * Description:第04课：Spring Boot整合Jsp
 */
@Controller
@RequestMapping("web")
public class WebController {

    @RequestMapping(value = "upload",method = RequestMethod.GET)
    public String index(){
        return "index2";
    }
}
