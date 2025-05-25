package com.example.cpsplatform.ai.service;

import com.example.cpsplatform.ai.controller.request.FaqRequest;
import com.example.cpsplatform.ai.controller.response.FaqResponse;

public interface AiApiService {

    String getTest(String reqeust);
    TestResponse postTest(TestRequest request);

    FaqResponse getAnswerFromFaqChatBot(FaqRequest request);
}
