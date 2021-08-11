package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

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
        return answerDao.createAnswer(answer);
    }

    public Answer editAnswerContent(final Answer answer, final String answerId, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to edit an answer");
        Answer editAnswer = answerDao.getAnswerByUuid(answerId);
        if(editAnswer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        else {
            if(editAnswer.getUser().getUuid() != userAuthEntity.getUser().getUuid()) {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }
        }
        editAnswer.setAns(answer.getAns());
        return answerDao.updateAnswerContent(editAnswer);
    }

    public String deleteAnswer(final String answerId, final String authorization) throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to delete an answer");
        Answer answer = answerDao.getAnswerByUuid(answerId);
        if(answer == null) {
            throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
        }
        else {
            if("nonadmin".equalsIgnoreCase(userAuthEntity.getUser().getRole()) || answer.getUser().getId() == userAuthEntity.getUser().getId()) {
                answerDao.deleteAnswer(answer);
                return answer.getUuid();
            }
            throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
        }
    }

    public List<Answer> getAllAnswersToQuestions(final String questionId, final String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to get the answers");
        Question question = questionDao.getQuestionByUUID(questionId);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
        }
        return answerDao.getAllAnswersToQuestions(question.getId());
    }
}
