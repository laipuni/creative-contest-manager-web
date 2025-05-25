package com.example.cpsplatform.ai.service;

import static org.junit.jupiter.api.Assertions.*;

import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class WebClientAiApiServiceTest {

    @Autowired
    private AiApiService aiApiService;

    @Test
    @DisplayName("AI 서버에서 응답을 정상적으로 받는지 확인한다.")
    void getAnswerFromFaqChatBot() {
        // given
        FaqRequest request = new FaqRequest("예선대회 결과는 언제 나오나요?");

        // when
        FaqResponse response = aiApiService.getAnswerFromFaqChatBot(request);

        // then
        assertNotNull(response);
        System.out.println("AI 응답: " + response.getResponse());
    }

}