package com.upgrad.quora.service.business;

import com.upgrad.quora.service.common.GenericErrorCode;
import com.upgrad.quora.service.common.UnexpectedException;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.*;
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

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signin(String authorization) throws AuthenticationFailedException {
        try {
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
                return userDao.createAuthToken(userAuthEntity);
            } else {
                throw new AuthenticationFailedException("ATH-002", "Password failed");
            }
        } catch (ArrayIndexOutOfBoundsException | IllegalArgumentException ex) {
            GenericErrorCode genericErrorCode = GenericErrorCode.GEN_001;
            throw new UnexpectedException(genericErrorCode, ex);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public String getUserUUID(String authorization) throws SignOutRestrictedException {
        String[] bearerToken = authorization.split("Bearer ");
        if(bearerToken != null && bearerToken.length > 1) {
            authorization = bearerToken[1];
        }
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if(userAuthEntity != null && userAuthEntity.getLogoutAt() == null) {
            userAuthEntity.setLogoutAt(ZonedDateTime.now());
            userDao.updateUserAuthEntity(userAuthEntity);
            return userAuthEntity.getUuid();
        } else {
            throw new SignOutRestrictedException("SGR-001", "User is not Signed in");
        }
    }

    public UserAuthEntity validateUserAuthentication(String authorization, String athr002Message) throws AuthorizationFailedException {
        String[] bearerToken = authorization.split("Bearer ");
        if(bearerToken != null && bearerToken.length > 1) {
            authorization = bearerToken[1];
        }
        UserAuthEntity userAuthEntity = userDao.getUserAuthToken(authorization);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        }
        if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", athr002Message);
        }
        return userAuthEntity;
    }

    public User getUser(final String userUuid, final String authorization) throws UserNotFoundException, AuthorizationFailedException {
        UserAuthEntity userAuthEntity = validateUserAuthentication(authorization, "User is signed out. Sign in first to get user details");
        final User user = userDao.getUserByUUID(userUuid);
        if(user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid does not exist");
        }
        return user;
    }
}
