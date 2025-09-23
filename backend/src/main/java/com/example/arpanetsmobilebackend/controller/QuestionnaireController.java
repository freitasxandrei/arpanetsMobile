package com.example.arpanetsmobilebackend.controller;

import com.example.arpanetsmobilebackend.dto.QuestionnaireSubmissionDTO;
import com.example.arpanetsmobilebackend.model.Questionnaire;
import com.example.arpanetsmobilebackend.service.QuestionnaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/questionnaires") 
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping
    public ResponseEntity<?> submitQuestionnaire(@RequestBody QuestionnaireSubmissionDTO submissionDTO) {
        try {
            Questionnaire result = questionnaireService.submitQuestionnaire(submissionDTO);
            return ResponseEntity.status(HttpStatus.CREATED).body(result);
        } catch (RuntimeException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
        }
    }
}
