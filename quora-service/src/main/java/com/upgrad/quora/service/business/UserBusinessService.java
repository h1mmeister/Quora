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

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public User signup(User user) {
        
        String password = user.getPassword();
        if(password != null) {
            String[] encryptedText = cryptographyProvider.encrypt(password);
            user.setSalt(encryptedText[0]);
            user.setPassword(encryptedText[1]);
            user.setRole("nonadmin");
        }
        return userDao.createUser(user);
    }
}
