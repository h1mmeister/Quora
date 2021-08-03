package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    // persist the question in the database
    public Question createQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }
}
