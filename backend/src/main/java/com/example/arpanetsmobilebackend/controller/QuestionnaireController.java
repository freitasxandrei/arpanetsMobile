package com.example.arpanetsmobilebackend.controller;

import com.example.arpanetsmobilebackend.dto.QuestionnaireResponseDTO;
import com.example.arpanetsmobilebackend.dto.QuestionnaireSubmissionDTO;
import com.example.arpanetsmobilebackend.service.QuestionnaireService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/questionnaires")
public class QuestionnaireController {

    private final QuestionnaireService questionnaireService;

    public QuestionnaireController(QuestionnaireService questionnaireService) {
        this.questionnaireService = questionnaireService;
    }

    @PostMapping
    public ResponseEntity<QuestionnaireResponseDTO> submitQuestionnaire(@RequestBody QuestionnaireSubmissionDTO submissionDTO) {
        QuestionnaireResponseDTO createdQuestionnaire = questionnaireService.createQuestionnaire(submissionDTO);
        return new ResponseEntity<>(createdQuestionnaire, HttpStatus.CREATED);
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<QuestionnaireResponseDTO>> getQuestionnairesByUser(@PathVariable Long userId) {
        List<QuestionnaireResponseDTO> questionnaires = questionnaireService.getQuestionnairesByUserId(userId);
        return ResponseEntity.ok(questionnaires);
    }

    @GetMapping("/{id}")
    public ResponseEntity<QuestionnaireResponseDTO> getQuestionnaireById(@PathVariable Long id) {
        QuestionnaireResponseDTO questionnaire = questionnaireService.getQuestionnaireById(id);
        return ResponseEntity.ok(questionnaire);
    }

    @PutMapping("/{id}")
    public ResponseEntity<QuestionnaireResponseDTO> updateQuestionnaire(@PathVariable Long id, @RequestBody QuestionnaireSubmissionDTO submissionDTO) {
        QuestionnaireResponseDTO updatedQuestionnaire = questionnaireService.updateQuestionnaire(id, submissionDTO);
        return ResponseEntity.ok(updatedQuestionnaire);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuestionnaire(@PathVariable Long id) {
        questionnaireService.deleteQuestionnaire(id);
        return ResponseEntity.noContent().build(); 
    }
}

