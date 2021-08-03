package com.upgrad.quora.service.business;

import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.stereotype.Service;

@Service
public class QuestionBusinessService {

    private UserBusinessService userBusinessService;

    public Question createNewQuestion(Question question, String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to post a question.");



    }
}
