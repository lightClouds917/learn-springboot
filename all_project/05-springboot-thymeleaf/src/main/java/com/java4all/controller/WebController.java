package com.java4all.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.HashMap;
import java.util.Map;

/**
 * Author: momo
 * Date: 2018/7/11
 * Description:第05课：Spring Boot整合Thymeleaf
 */
@Controller
@RequestMapping(value = "web")
public class WebController {

    @RequestMapping(value = "hello")
    public String hello(Model model){
        Map map = new HashMap();
        map.put("province","浙江省");
        map.put("city","杭州");

        //当然，这里可以是任何数据类型，我以map为例
        model.addAttribute("map",map);
        return "hello";
    }
}
