package com.example.studyplanner.controller;

import com.example.studyplanner.dto.LearningPlanDTO;
import com.example.studyplanner.service.LearningPlanService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/plan")
public class LearningPlanController {

    private final LearningPlanService learningPlanService;

    public LearningPlanController(LearningPlanService learningPlanService) {
        this.learningPlanService = learningPlanService;
    }

    @GetMapping
    public ResponseEntity<LearningPlanDTO> getPlan(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(
                learningPlanService.generateLearningPlan(userId)
        );
    }

    @PostMapping("/recalculate")
    public ResponseEntity<LearningPlanDTO> recalculatePlan(
            Authentication authentication
    ) {
        UUID userId = UUID.fromString(authentication.getName());

        return ResponseEntity.ok(
                learningPlanService.recalculatePlan(userId)
        );
    }
}