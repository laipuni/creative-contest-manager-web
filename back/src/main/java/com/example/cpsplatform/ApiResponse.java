package com.example.cpsplatform;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;

/**
 * 공통 API 응답 객체
 *
 * @param <T> 응답 데이터의 타입
 */
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ApiResponse<T> {

    /**
     * HTTP 상태 코드 (예: 200 OK, 400 BAD_REQUEST 등)
     */
    private HttpStatus status;

    /**
     * HTTP 상태 코드의 정수 값 (예: 200, 400 등)
     */
    private int code;

    /**
     * 응답 데이터
     */
    private T data;

    /**
     * 생성자 (Builder 패턴용)
     *
     * @param status HTTP 상태 코드
     * @param code   HTTP 상태 코드의 정수 값
     * @param data   응답 데이터
     */
    @Builder
    private ApiResponse(final HttpStatus status, final int code, final T data) {
        this.status = status;
        this.code = code;
        this.data = data;
    }

    /**
     * 커스텀 상태 코드와 데이터로 응답 객체 생성
     *
     * @param status HTTP 상태 코드
     * @param data   응답 데이터
     * @return ApiResponse 객체
     * @param <T>    응답 데이터 타입
     */
    public static <T> ApiResponse<T> of(final HttpStatus status, T data) {
        return new ApiResponse<>(status, status.value(), data);
    }

    /**
     * 상태 코드 200 OK와 함께 응답 객체 생성
     *
     * @param data 응답 데이터
     * @return 200 OK 응답 객체
     * @param <T>  응답 데이터 타입
     */
    public static <T> ApiResponse<T> ok(T data) {
        return new ApiResponse<>(HttpStatus.OK, HttpStatus.OK.value(), data);
    }
}

