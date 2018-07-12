package com.java4all.dao;

import com.java4all.entity.User;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Author: yunqing
 * Date: 2018/7/12
 * Description:
 */
@Repository
public interface UserDao {
    List<User> getUserList();
}
