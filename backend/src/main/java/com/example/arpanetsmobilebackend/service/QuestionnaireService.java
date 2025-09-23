package com.example.arpanetsmobilebackend.service;

import com.example.arpanetsmobilebackend.dto.QuestionnaireSubmissionDTO;
import com.example.arpanetsmobilebackend.model.Answer;
import com.example.arpanetsmobilebackend.model.Questionnaire;
import com.example.arpanetsmobilebackend.model.User;
import com.example.arpanetsmobilebackend.repository.AnswerRepository;
import com.example.arpanetsmobilebackend.repository.QuestionnaireRepository;
import com.example.arpanetsmobilebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
public class QuestionnaireService {

    private final UserRepository userRepository;
    private final QuestionnaireRepository questionnaireRepository;
    private final AnswerRepository answerRepository;

    public QuestionnaireService(UserRepository userRepository, QuestionnaireRepository questionnaireRepository, AnswerRepository answerRepository) {
        this.userRepository = userRepository;
        this.questionnaireRepository = questionnaireRepository;
        this.answerRepository = answerRepository;
    }

    @Transactional 
    public Questionnaire submitQuestionnaire(QuestionnaireSubmissionDTO submissionDTO) {
        User user = userRepository.findById(submissionDTO.getUserId())
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado com o ID: " + submissionDTO.getUserId()));

        int totalScore = (int) submissionDTO.getAnswers().stream().filter(answer -> answer).count();

        String sufferingLevel;
        if (totalScore <= 7) {
            sufferingLevel = "Sofrimento mental leve";
        } else if (totalScore <= 14) {
            sufferingLevel = "Sofrimento mental moderado";
        } else {
            sufferingLevel = "Sofrimento mental severo";
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setUser(user);
        questionnaire.setTotalScore(totalScore);
        questionnaire.setSufferingLevel(sufferingLevel);
        
        Questionnaire savedQuestionnaire = questionnaireRepository.save(questionnaire);

        List<Answer> answerEntities = new ArrayList<>();
        for (int i = 0; i < submissionDTO.getAnswers().size(); i++) {
            Answer answer = new Answer();
            answer.setQuestionnaire(savedQuestionnaire);
            answer.setQuestionNumber(i + 1);
            answer.setAnswer(submissionDTO.getAnswers().get(i));
            answerEntities.add(answer);
        }
        answerRepository.saveAll(answerEntities); 
        
        savedQuestionnaire.setAnswers(answerEntities);
        return savedQuestionnaire;
    }
}
