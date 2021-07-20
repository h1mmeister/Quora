package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.Base64;

@Service
public class UserBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider cryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public User signup(User user) throws SignUpRestrictedException {
        if(userDao.getUserByUserName(user.getUserName()) != null) {
            throw new SignUpRestrictedException("SGR-001", "Try any other Username, this Username has already been taken");
        }
        if(userDao.getUserByEmail(user.getEmail()) != null) {
            throw new SignUpRestrictedException("SGR-002", "This user has already been registered, try with any other emailId");
        }
        String password = user.getPassword();
        if(password != null) {
            String[] encryptedText = cryptographyProvider.encrypt(password);
            user.setSalt(encryptedText[0]);
            user.setPassword(encryptedText[1]);
            user.setRole("nonadmin");
        }
        return userDao.createUser(user);
    }

    public UserAuthEntity signin(String authorization) throws AuthenticationFailedException {
        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodeText = new String(decode);
        String[] decodedArray = decodeText.split(":");
        String username = decodedArray[0];
        String password = decodedArray[1];
        User user = userDao.getUserByUserName(username);
        if(user == null) {
            throw new AuthenticationFailedException("ATH-001", "This username does not exist");
        }
        final String encryptedPassword = cryptographyProvider.encrypt(password, user.getSalt());
        if(encryptedPassword.equals(user.getPassword())) {
        JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
        UserAuthEntity userAuthEntity = new UserAuthEntity();
        userAuthEntity.setUser(user);
        final ZonedDateTime now = ZonedDateTime.now();
        final ZonedDateTime expiresAt = now.plusHours(8);
        userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(user.getUuid(), now, expiresAt));
        userAuthEntity.setLoginAt(now);
        userAuthEntity.setExpiresAt(expiresAt);
        userAuthEntity.setUuid(user.getUuid());
        }

    }
}
