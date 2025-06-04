package com.example.cpsplatform.exception.controller;

import com.example.cpsplatform.exception.*;
import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.example.cpsplatform.exception.controller.dto.UniqueConstraintMessage;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
@RequiredArgsConstructor
public class GlobalExceptionHandler {

    @ExceptionHandler(DataIntegrityViolationException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Object> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        String message = null;
        String constraintName = extractConstraintName(e);

        if (constraintName != null) {
            log.debug("[DataIntegrityViolationException] 유니크 제약 조건 위반: {}", constraintName);
            //ex) contenst.uk_xxxx_xxxx -> uk_xxxx_xxxx 분리
            message = UniqueConstraintMessage.findUniqueConstraintMessage(constraintName.split("\\.")[1]);
        }

        if(StringUtils.hasText(message)){
            //Unique 제약 조건에 맞는 메세지를 찾았을 경우
            return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, message, null);
        } else{
            log.warn("[DataIntegrityViolationException] 기타 제약 조건 위반", e);
            return ApiErrorResponse.of(HttpStatus.BAD_REQUEST, "요청한 데이터에 문제가 있습니다. 입력값을 확인해주세요.", null);        }
    }

    private String extractConstraintName(Throwable e) {
        while (e != null) {
            if (e instanceof ConstraintViolationException) {
                return ((ConstraintViolationException) e).getConstraintName();
            }
            e = e.getCause();
        }
        return null;
    }

    @ExceptionHandler(UnsupportedCertificateTypeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> UnsupportedCertificateTypeException(UnsupportedCertificateTypeException ex) {
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                null
        );
    }

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

    @ExceptionHandler(AiServerException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse<Object> handleAiServerException(AiServerException ex) {
        log.error("{}",ex.getMessage(),ex);
        return ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                ex.getMessage(),
                null);
    }

    @ExceptionHandler(ClientRequestException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiErrorResponse<Object> handleClientRequest(ClientRequestException ex) {
        return ApiErrorResponse.of(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                null
        );
    }
}
