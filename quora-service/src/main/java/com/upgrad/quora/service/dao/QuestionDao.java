package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.Question;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    // persist the question in the database
    public Question createQuestion(Question question) {
        entityManager.persist(question);
        return question;
    }

    // get the question list from the database
    public List<Question> getAllQuestions() {
        List<Question> allQuestions = entityManager.createQuery("SELECT q FROM Question q", Question.class).getResultList();
        return allQuestions;
    }

    // get the question by uuid
    public Question getQuestionByUUID(String questionId) {
        try {
            return entityManager.createNamedQuery("questionByUUID", Question.class).setParameter("uuid", questionId).getSingleResult();
        } catch (NoResultException nre) {
            return null;
        }
    }

    // update the existing question
    public Question updateEditedQuestion(Question question) {
        entityManager.merge(question);
        return question;
    }
}
