package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AnswerBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private AnswerDao answerDao;

    public Answer createAnswer(final Answer answer, final String questionId, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to post an answer");
        Question question = questionDao.getQuestionByUUID(questionId);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
        }
        answer.setQuestion(question);
        answer.setUser(userAuthEntity.getUser());
        return answerDao.createAnswer(answer)
    }
}
