package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createNewQuestion(Question question, String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to post a question.");
        question.setDate(ZonedDateTime.now());
        question.setUser(userAuthEntity.getUser());
        Question createdQuestion = questionDao.createQuestion(question);
        return createdQuestion;
    }
}
