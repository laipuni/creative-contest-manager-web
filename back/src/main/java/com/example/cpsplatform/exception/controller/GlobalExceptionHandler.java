package com.example.cpsplatform.exception.controller;

import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> IllegalStateException(IllegalStateException e){
        log.error(e.getMessage());
        //todo 개발자에게 해당 에러를 전송해야 함
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Map<String, String>> handleValidationException(MethodArgumentNotValidException ex) {
        return ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getBindingResult().getFieldErrors()
                        .get(0).getDefaultMessage(),
                null
        );
    }

}
