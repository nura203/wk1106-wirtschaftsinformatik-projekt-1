package com.example.studyplanner.controller;

import com.example.studyplanner.dto.LearningPlanDTO;
import com.example.studyplanner.service.LearningPlanService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class LearningPlanControllerTest {

    @Mock
    private LearningPlanService learningPlanService;

    @Mock
    private Authentication authentication;

    @InjectMocks
    private LearningPlanController learningPlanController;

    private MockMvc mockMvc;

    private UUID userId;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders
                .standaloneSetup(learningPlanController)
                .build();

        userId = UUID.randomUUID();

        when(authentication.getName())
                .thenReturn(userId.toString());
    }

    @Test
    void getPlanReturnsOk() throws Exception {
        LearningPlanDTO plan = new LearningPlanDTO(
                LocalDate.now(),
                List.of()
        );

        when(learningPlanService.generateLearningPlan(userId))
                .thenReturn(plan);

        mockMvc.perform(
                        get("/plan")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(learningPlanService)
                .generateLearningPlan(userId);
    }

    @Test
    void recalculatePlanReturnsOk() throws Exception {
        LearningPlanDTO plan = new LearningPlanDTO(
                LocalDate.now(),
                List.of()
        );

        when(learningPlanService.recalculatePlan(userId))
                .thenReturn(plan);

        mockMvc.perform(
                        post("/plan/recalculate")
                                .principal(authentication)
                )
                .andExpect(status().isOk());

        verify(learningPlanService)
                .recalculatePlan(userId);
    }
}