package com.example.arpanetsmobilebackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class QuestionnaireResponseDTO {
    private Long id;
    private Long userId;
    private LocalDateTime responseDate;
    private int totalScore;
    private String sufferingLevel;
    private List<AnswerDTO> answers;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public LocalDateTime getResponseDate() {
        return responseDate;
    }

    public void setResponseDate(LocalDateTime responseDate) {
        this.responseDate = responseDate;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getSufferingLevel() {
        return sufferingLevel;
    }

    public void setSufferingLevel(String sufferingLevel) {
        this.sufferingLevel = sufferingLevel;
    }

    public List<AnswerDTO> getAnswers() {
        return answers;
    }

    public void setAnswers(List<AnswerDTO> answers) {
        this.answers = answers;
    }
}

