package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
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
    private final LoginFailService loginFailService;

    public CustomAuthenticationFailHandler(final ObjectMapper objectMapper, final LoginFailService loginFailService) {
        this.objectMapper = objectMapper;
        this.loginFailService = loginFailService;
    }

    @Override
    public void onAuthenticationFailure(final HttpServletRequest request, final HttpServletResponse response, final AuthenticationException exception) throws IOException {
        String clientIp = request.getRemoteAddr();
        String username = (String) request.getAttribute(USERNAME_VALUE);
        if(username != null){
            loginFailService.failLogin(username,clientIp);
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        ApiErrorResponse<Object> apiErrorResponse = ApiErrorResponse.of(HttpStatus.UNAUTHORIZED, exception.getMessage(), null);
        response.getWriter().write(objectMapper.writeValueAsString(apiErrorResponse));
    }
}
