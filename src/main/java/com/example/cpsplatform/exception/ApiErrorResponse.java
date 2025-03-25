package com.example.cpsplatform.exception;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiErrorResponse<T> {

    private HttpStatus status;
    private int code;
    private String message;
    private T data;

    @Builder
    private ApiErrorResponse(final HttpStatus status, final String message, final int code, final T data) {
        this.status = status;
        this.message = message;
        this.code = code;
        this.data = data;
    }

    public static <T> ApiErrorResponse<T> of(final HttpStatus status,String msg, T data){
        return new ApiErrorResponse<>(status, msg, status.value(), data);
    }

}
