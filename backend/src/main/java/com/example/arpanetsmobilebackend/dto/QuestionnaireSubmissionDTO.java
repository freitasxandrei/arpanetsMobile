package com.example.arpanetsmobilebackend.dto;

import java.util.List;

public class QuestionnaireSubmissionDTO {

    private Long userId;
    private List<Boolean> answers; 

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public List<Boolean> getAnswers() {
        return answers;
    }

    public void setAnswers(List<Boolean> answers) {
        this.answers = answers;
    }
}
