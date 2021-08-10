package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    // persisting the answer for a given question
    public Answer createAnswer(Answer answer) {
        entityManager.persist(answer);
        return answer;
    }
}
