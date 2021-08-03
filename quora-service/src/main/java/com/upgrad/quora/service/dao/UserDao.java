package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    // persists the user information in the database table
    public User createUser(User user) {
        entityManager.persist(user);
        return user;
    }

    // retrieves the user record matching with the username passed
    public User getUserByUserName(final String username) {
        try {
            return entityManager.createNamedQuery("userByUserName", User.class).setParameter("userName", username).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    // retrieves the user record matching with the email passed
    public User getUserByEmail(final String email) {
        try {
            return entityManager.createNamedQuery("userByEmail", User.class).setParameter("email", email).getSingleResult();
        } catch(NoResultException nre) {
            return null;
        }
    }

    // retrieves the user detail matched with the userId passed
    public User getUserByUUID(final String userUuid) {
        try {
            return entityManager.createNamedQuery("userByUUID", User.class).setParameter("uuid", userUuid).getSingleResult();
        }catch(NoResultException nre) {
            return null;
        }
    }

    // persist the auth data in database
    public UserAuthEntity createAuthToken(final UserAuthEntity userAuthEntity) {
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    // retrieves the user auth record matched with the access token passed
    public UserAuthEntity getUserAuthToken(final String accessToken) {
        try {
            return entityManager.createNamedQuery("userAuthByAccessToken", UserAuthEntity.class).setParameter("accessToken", accessToken).getSingleResult();
        }catch(NoResultException nre) {
            return null;
        }
    }

    public void updateUserAuthEntity(final UserAuthEntity updatedUserAuthEntity){
        entityManager.merge(updatedUserAuthEntity);
    }

    public void deleteUser(User user) {
        entityManager.remove(user);
    }

}
