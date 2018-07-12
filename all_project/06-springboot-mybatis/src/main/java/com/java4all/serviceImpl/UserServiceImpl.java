package com.java4all.serviceImpl;

import com.java4all.dao.UserDao;
import com.java4all.entity.User;
import com.java4all.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Author: yunqing
 * Date: 2018/7/12
 * Description:
 */
@Service
public class UserServiceImpl implements UserService{

    @Autowired
    private UserDao userDao;

    @Override
    public List<User> getUserList() {
        List<User> list = userDao.getUserList();
        return list;
    }
}
