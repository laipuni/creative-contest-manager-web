package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;

import java.io.IOException;

import static com.example.cpsplatform.security.config.SecurityConfig.USERNAME_VALUE;

@Slf4j
public class CustomAuthenticationFailHandler implements AuthenticationFailureHandler {

    private final ObjectMapper objectMapper;

    public CustomAuthenticationFailHandler(final ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException, ServletException {
        //todo 로그인 실패시 누적 로직 필요
        String username = request.getParameter(USERNAME_VALUE);
        String clientIp = request.getRemoteAddr();
        log.debug("로그인 실패 - IP: {}, loginId: {}, 이유: {}", clientIp, username, exception.getMessage());
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiErrorResponse<Object> apiErrorResponse = ApiErrorResponse.of(HttpStatus.UNAUTHORIZED, exception.getMessage(), null);
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
