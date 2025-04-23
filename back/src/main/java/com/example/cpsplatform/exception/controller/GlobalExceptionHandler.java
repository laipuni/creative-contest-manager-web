package com.example.cpsplatform.exception.controller;

import com.example.cpsplatform.exception.CryptoException;
import com.example.cpsplatform.exception.FileDownloadException;
import com.example.cpsplatform.exception.FileNotFoundException;
import com.example.cpsplatform.exception.FileReadException;
import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(FileNotFoundException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Object> fileNotFoundException(FileNotFoundException ex, HttpServletResponse response) {
        //이미 설정된 콘텐츠 타입을 재설정(application/zip or octet-stream -> application/Json")
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(FileDownloadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> fileDownloadException(FileDownloadException ex, HttpServletResponse response) {
        log.error("파일 다운로드 중 오류 발생", ex);
        //todo 개발자에게 해당 에러를 전송해야 함

        //이미 설정된 콘텐츠 타입을 재설정(application/zip or octet-stream -> application/Json")
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                null
        );
    }

    @ExceptionHandler(FileReadException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> handleFileReadException(FileReadException e) {
        log.error("msg = {}",e.getMessage(),e);
        //todo 개발자에게 해당 에러를 전송해야 함
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                e.getMessage(),
                null
        );
    }

    @ExceptionHandler(CryptoException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> handleCryptoException(CryptoException e) {
        log.error("암호화 처리 중 예외 발생", e);
        //todo 개발자에게 해당 에러를 전송해야 함
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버에 문제가 생겨 해당서비스를 이용할 수 없습니다. 죄송합니다.",
                null
        );
    }

    @ExceptionHandler(IllegalStateException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> illegalStateException(IllegalStateException e){
        log.error("서버 내부 오류 발생", e);
        //todo 개발자에게 해당 에러를 전송해야 함
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "서버에 문제가 생겨 해당서비스를 이용할 수 없습니다. 죄송합니다.",
                null
        );
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Object> handleValidationException(MethodArgumentNotValidException ex) {
        String defaultMessage = ex.getBindingResult().getFieldErrors()
                .get(0).getDefaultMessage();
        log.trace("{}",defaultMessage);
        return ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                defaultMessage,
                null
        );
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Object> runtimeException(RuntimeException ex) {
        log.trace("{}",ex.getMessage());
        return ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }

}
