package com.example.cpsplatform.ai.service;


public interface AiApiService {

    String getTest(String reqeust);
    TestResponse postTest(TestRequest request);
}
