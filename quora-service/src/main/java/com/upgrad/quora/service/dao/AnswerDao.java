package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Answer;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
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

    // getting the answer from the database using the answer uuid
    public Answer getAnswerByUuid(String answerId) {
        try {
            return entityManager.createNamedQuery("answerByUUID", Answer.class).setParameter("uuid", answerId).getSingleResult();
        } catch(NoResultException nre){
            return null;
        }
    }

    // updating the new content for answer
    public Answer updateAnswerContent(Answer answer) {
        entityManager.merge(answer);
        return answer;
    }
}
