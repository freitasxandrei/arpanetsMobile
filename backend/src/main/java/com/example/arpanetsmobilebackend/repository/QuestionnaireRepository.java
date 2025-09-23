package com.example.arpanetsmobilebackend.repository;

import com.example.arpanetsmobilebackend.model.Questionnaire;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface QuestionnaireRepository extends JpaRepository<Questionnaire, Long> {
    
    List<Questionnaire> findByUserId(Long userId);

}

