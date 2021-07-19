package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public User signup(User user) {
        

        return userDao.createUser(user);
    }
}
