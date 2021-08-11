package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.entity.Answer;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(final AnswerRequest answerRequest, @PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
        final Answer answer = new Answer();
        answer.setAns(answerRequest.getAnswer());
        answer.setUuid(UUID.randomUUID().toString());
        answer.setDate(ZonedDateTime.now());
        Answer updatedAnswer = answerBusinessService.createAnswer(answer, questionId, authorization);
        AnswerResponse answerResponse = new AnswerResponse();
        answerResponse.id(updatedAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.PUT, path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswerContent(final AnswerEditRequest answerEditRequest, @PathVariable("answerId") final String answerId, @RequestHeader("authorization") final String authorization) throws AnswerNotFoundException, AuthorizationFailedException {
        final Answer answer = new Answer();
        answer.setAns(answerEditRequest.getContent());
        Answer editedAnswer = answerBusinessService.editAnswerContent(answer, answerId, authorization);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse();
        answerEditResponse.id(editedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE, path = "/answer/delete/{answerId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") final String answerId, final String authorization) throws AnswerNotFoundException, AuthorizationFailedException {
       String UUID = answerBusinessService.deleteAnswer(answerId, authorization);
       AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse();
       answerDeleteResponse.id(answerDeleteResponse.getId()).status("ANSWER DELETED");
       return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET, path = "answer/all/{questionId}", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") final String questionId, @RequestHeader("authorization") final String authorization) throws InvalidQuestionException, AuthorizationFailedException {
       List<Answer> allAnswersToQuestions = answerBusinessService.getAllAnswersToQuestions(questionId, authorization);
        List<AnswerDetailsResponse> answerDetailsResponseList = new ArrayList<AnswerDetailsResponse>();
        for(Answer answer: allAnswersToQuestions) {
            AnswerDetailsResponse answerDetailsResponse = new AnswerDetailsResponse();
            answerDetailsResponse.id(answer.getUuid()).questionContent(answer.getQuestion().getContent()).answerContent(answer.getAns());
            answerDetailsResponseList.add(answerDetailsResponse);
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponseList, HttpStatus.OK);
    }

}
