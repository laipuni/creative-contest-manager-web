package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.security.service.LoginFailService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
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

        //인증 정보를 SecurityContext에 수동 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        //CSRF 토큰을 응답 헤더에 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            response.setHeader("X-XSRF-TOKEN", csrfToken.getToken());
        }

        //세션 쿠키 내려주기
        String sessionId = session.getId();
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        response.addCookie(sessionCookie);

        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
