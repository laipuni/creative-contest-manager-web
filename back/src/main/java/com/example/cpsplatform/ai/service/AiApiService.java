package com.example.cpsplatform.ai.service;

import com.example.cpsplatform.TestRequest;
import com.example.cpsplatform.TestResponse;


public interface AiApiService {

    String getTest(String reqeust);
    TestResponse postTest(TestRequest request);
}
