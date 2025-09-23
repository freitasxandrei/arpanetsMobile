package com.example.arpanetsmobilebackend.service;

import com.example.arpanetsmobilebackend.dto.AnswerDTO;
import com.example.arpanetsmobilebackend.dto.QuestionnaireResponseDTO;
import com.example.arpanetsmobilebackend.dto.QuestionnaireSubmissionDTO;
import com.example.arpanetsmobilebackend.exception.ResourceNotFoundException;
import com.example.arpanetsmobilebackend.model.Answer;
import com.example.arpanetsmobilebackend.model.Questionnaire;
import com.example.arpanetsmobilebackend.model.User;
import com.example.arpanetsmobilebackend.repository.AnswerRepository;
import com.example.arpanetsmobilebackend.repository.QuestionnaireRepository;
import com.example.arpanetsmobilebackend.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class QuestionnaireService {

    private final QuestionnaireRepository questionnaireRepository;
    private final UserRepository userRepository;
    private final AnswerRepository answerRepository;

    public QuestionnaireService(QuestionnaireRepository questionnaireRepository, UserRepository userRepository, AnswerRepository answerRepository) {
        this.questionnaireRepository = questionnaireRepository;
        this.userRepository = userRepository;
        this.answerRepository = answerRepository;
    }
    
    @Transactional
    public QuestionnaireResponseDTO createQuestionnaire(QuestionnaireSubmissionDTO submissionDTO) {
        User user = userRepository.findById(submissionDTO.getUserId())
                .orElseThrow(() -> new ResourceNotFoundException("Utilizador não encontrado com o id: " + submissionDTO.getUserId()));

        if (submissionDTO.getAnswers() == null || submissionDTO.getAnswers().size() != 20) {
            throw new IllegalArgumentException("A lista de respostas deve conter exatamente 20 itens.");
        }

        Questionnaire questionnaire = new Questionnaire();
        questionnaire.setUser(user);
        questionnaire.setResponseDate(LocalDateTime.now());

        int score = (int) submissionDTO.getAnswers().stream().filter(Boolean::booleanValue).count();
        questionnaire.setTotalScore(score);
        questionnaire.setSufferingLevel(calculateSufferingLevel(score));

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
        return convertToDto(savedQuestionnaire);
    }
    
    @Transactional(readOnly = true)
    public List<QuestionnaireResponseDTO> getQuestionnairesByUserId(Long userId) {
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Utilizador não encontrado com o id: " + userId);
        }
        List<Questionnaire> questionnaires = questionnaireRepository.findByUserId(userId);
        return questionnaires.stream()
                .map(this::convertToDto)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public QuestionnaireResponseDTO getQuestionnaireById(Long id) {
        Questionnaire questionnaire = questionnaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionário não encontrado com o id: " + id));
        return convertToDto(questionnaire);
    }

    @Transactional
    public QuestionnaireResponseDTO updateQuestionnaire(Long id, QuestionnaireSubmissionDTO submissionDTO) {
        Questionnaire questionnaire = questionnaireRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Questionário não encontrado com o id: " + id));

        if (submissionDTO.getAnswers() == null || submissionDTO.getAnswers().size() != 20) {
            throw new IllegalArgumentException("A lista de respostas deve conter exatamente 20 itens.");
        }
        
        questionnaire.getAnswers().clear();
        answerRepository.flush();

        int score = (int) submissionDTO.getAnswers().stream().filter(Boolean::booleanValue).count();
        questionnaire.setTotalScore(score);
        questionnaire.setSufferingLevel(calculateSufferingLevel(score));
        questionnaire.setResponseDate(LocalDateTime.now());

        List<Answer> newAnswerEntities = new ArrayList<>();
        for (int i = 0; i < submissionDTO.getAnswers().size(); i++) {
            Answer answer = new Answer();
            answer.setQuestionnaire(questionnaire);
            answer.setQuestionNumber(i + 1);
            answer.setAnswer(submissionDTO.getAnswers().get(i));
            newAnswerEntities.add(answer);
        }
        questionnaire.getAnswers().addAll(newAnswerEntities);
        
        Questionnaire updatedQuestionnaire = questionnaireRepository.save(questionnaire);
        return convertToDto(updatedQuestionnaire);
    }

    @Transactional
    public void deleteQuestionnaire(Long id) {
        if (!questionnaireRepository.existsById(id)) {
            throw new ResourceNotFoundException("Questionário não encontrado com o id: " + id);
        }
        questionnaireRepository.deleteById(id);
    }

    private String calculateSufferingLevel(int score) {
        if (score <= 7) {
            return "Sofrimento mental leve";
        } else if (score <= 14) {
            return "Sofrimento mental moderado";
        } else {
            return "Sofrimento mental severo";
        }
    }
    
    private QuestionnaireResponseDTO convertToDto(Questionnaire questionnaire) {
        QuestionnaireResponseDTO dto = new QuestionnaireResponseDTO();
        dto.setId(questionnaire.getId());
        dto.setUserId(questionnaire.getUser().getId());
        dto.setResponseDate(questionnaire.getResponseDate());
        dto.setTotalScore(questionnaire.getTotalScore());
        dto.setSufferingLevel(questionnaire.getSufferingLevel());
        
        List<AnswerDTO> answerDTOs = questionnaire.getAnswers().stream()
            .map(answer -> new AnswerDTO(answer.getQuestionNumber(), answer.getAnswer()))
            .sorted((a1, a2) -> Integer.compare(a1.getQuestionNumber(), a2.getQuestionNumber()))
            .collect(Collectors.toList());
        dto.setAnswers(answerDTOs);
        
        return dto;
    }
}