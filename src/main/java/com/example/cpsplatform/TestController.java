package com.example.cpsplatform;

import com.example.cpsplatform.ai.service.AiApiService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class TestController {

    private final AiApiService apiService;

    @GetMapping("/api/test")
    public ApiResponse<Object> testGet(@RequestParam(value = "request",defaultValue = "") String request){
        log.info("it's just a get test, request = {}", request);
        String body = apiService.getTest(request);
        log.info("receive Ai server Response response = {} ", body);
        return ApiResponse.ok(body);
    }

    @PostMapping("/api/test")
    public ApiResponse<Object> testPost(@Valid @RequestBody TestRequest request){
        log.info("it's just a post test, request = {}", request);
        TestResponse body = apiService.postTest(request);
        log.info("receive Ai server Response response = {} ", body);
        return ApiResponse.ok(body);
    }
}
