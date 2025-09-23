package com.example.arpanetsmobilebackend.dto;

public class AnswerDTO {
    private int questionNumber;
    private boolean answer;

    public AnswerDTO() {
    }

    public AnswerDTO(int questionNumber, boolean answer) {
        this.questionNumber = questionNumber;
        this.answer = answer;
    }

    public int getQuestionNumber() {
        return questionNumber;
    }

    public void setQuestionNumber(int questionNumber) {
        this.questionNumber = questionNumber;
    }

    public boolean isAnswer() {
        return answer;
    }

    public void setAnswer(boolean answer) {
        this.answer = answer;
    }
}