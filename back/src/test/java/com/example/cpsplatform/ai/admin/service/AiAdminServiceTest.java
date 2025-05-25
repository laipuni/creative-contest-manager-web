package com.example.cpsplatform.ai.admin.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.ai.admin.controller.request.QuestionGenerateRequest;
import com.example.cpsplatform.ai.admin.service.dto.QuestionDto;
import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
import com.example.cpsplatform.ai.service.AiApiService;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiAdminServiceTest {

    @Autowired
    private AiAdminService aiAdminService;

    @Test
    @DisplayName("AI 서버에서 응답을 정상적으로 받는지 확인한다.(논리)")
    void generateQuestions() {
        // given
        QuestionGenerateRequest request = new QuestionGenerateRequest("논리", "하", 2);

        // when
        List<QuestionDto> result = aiAdminService.generateQuestions(request);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        for (QuestionDto dto : result) {
            System.out.println(dto.getQuestion());
            System.out.println(dto.getAnswer());
        }
    }

    @Test
    @DisplayName("AI 서버에서 응답을 정상적으로 받는지 확인한다.(수학)")
    void generateQuestions1() {
        // given
        QuestionGenerateRequest request = new QuestionGenerateRequest("수학", "중", 2);

        // when
        List<QuestionDto> result = aiAdminService.generateQuestions(request);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        for (QuestionDto dto : result) {
            System.out.println(dto.getQuestion());
            System.out.println(dto.getAnswer());
        }
    }

    @Test
    @DisplayName("AI 서버에서 응답을 정상적으로 받는지 확인한다.(상식)")
    void generateQuestions2() {
        // given
        QuestionGenerateRequest request = new QuestionGenerateRequest("상식", "상", 2);

        // when
        List<QuestionDto> result = aiAdminService.generateQuestions(request);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        for (QuestionDto dto : result) {
            System.out.println(dto.getQuestion());
            System.out.println(dto.getAnswer());
        }
    }

}