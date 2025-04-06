package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.security.config.SecurityConfig;
import com.example.cpsplatform.security.service.LoginFailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;

import static com.example.cpsplatform.security.config.SecurityConfig.USERNAME_VALUE;

@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginFailService loginFailService;

    public CustomAuthenticationSuccessHandler(final LoginFailService loginFailService) {
        this.loginFailService = loginFailService;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        String loginId = (String) request.getAttribute(USERNAME_VALUE);
        String clientIp = request.getRemoteUser();
        if(loginId != null){
            loginFailService.successLogin(loginId,clientIp);
        }

        // CSRF 토큰을 클라이언트로 응답 헤더에 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            response.setHeader("X-CSRF-TOKEN", csrfToken.getToken());  // 응답 헤더에 CSRF 토큰 전달
        }
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
