package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.entity.User;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private UserBusinessService userBusinessService;

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public Question createNewQuestion(Question question, String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to post a question.");
        question.setDate(ZonedDateTime.now());
        question.setUser(userAuthEntity.getUser());
        Question createdQuestion = questionDao.createQuestion(question);
        return createdQuestion;
    }

    public List<Question> getAllQuestions(String authorization) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to get all questions");
        return questionDao.getAllQuestions();
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public Question editedQuestion(Question question, String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to edit the question");
        Question questionToBeEdited = questionDao.getQuestionByUUID(questionId);
        if(questionToBeEdited == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        else {
            if(questionToBeEdited.getUser().getId() != userAuthEntity.getUser().getId()) {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        }
        questionToBeEdited.setContent(question.getContent());
        return questionDao.updateEditedQuestion(questionToBeEdited);
    }

    public String deleteQuestion(String questionId, String authorization) throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to delete a question");
        Question question = questionDao.getQuestionByUUID(questionId);
        if(question == null) {
            throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
        }
        if("admin".equalsIgnoreCase(userAuthEntity.getUser().getRole()) || question.getUser().getId() == userAuthEntity.getUser().getId()) {
            questionDao.deleteQuestion(question);
            return question.getUuid();
        }
        throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
    }

    public List<Question> getAllQuestionsByUser(String userId, String authorization) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userBusinessService.validateUserAuthentication(authorization, "User is signed out.Sign in first to get all questions posted by a specific user");
        User user = userDao.getUserByUUID(userId);
        if(user == null) {
            throw new UserNotFoundException("USR-001", "User with entered uuid whose question details are to be seen does not exist");
        }
        return questionDao.findQuestionsByUser(user.getId());
    }
}
