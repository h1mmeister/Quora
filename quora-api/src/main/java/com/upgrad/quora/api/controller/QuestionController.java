package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.QuestionDetailsResponse;
import com.upgrad.quora.api.model.QuestionRequest;
import com.upgrad.quora.api.model.QuestionResponse;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.Question;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST, path = "/question/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE, produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(final QuestionRequest questionRequest, @RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        final Question question = new Question();
        question.setUuid(UUID.randomUUID().toString());
        question.setContent(questionRequest.getContent());
        Question createdQuestion = questionBusinessService.createNewQuestion(question, authorization);
        QuestionResponse questionResponse = new QuestionResponse();
        questionResponse.id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET, path = "/question/all", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") final String authorization) throws AuthorizationFailedException {
        List<Question> allQuestions = questionBusinessService.getAllQuestions(authorization);
        List<QuestionDetailsResponse> allQuestionDetailsResponse = new ArrayList<>();
        for(Question question : allQuestions) {
            QuestionDetailsResponse questionDetailsResponse = new QuestionDetailsResponse();
            questionDetailsResponse.id(question.getUuid()).content(question.getContent());
            allQuestionDetailsResponse.add(questionDetailsResponse);
        }
        return new ResponseEntity<List<QuestionDetailsResponse>>(allQuestionDetailsResponse, HttpStatus.OK);
    }
}
