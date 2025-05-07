package com.example.cpsplatform.security.handler;

import com.example.cpsplatform.exception.controller.dto.ApiErrorResponse;
import com.example.cpsplatform.security.handler.response.LoginProfile;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.security.web.csrf.CsrfToken;

import java.io.IOException;
import java.util.Collection;

import static com.example.cpsplatform.security.config.SecurityConfig.USERNAME_VALUE;

/**
 * 로그인을 성공했을 때, 로그인 성공을 후처리할 핸들러
 * 반환 값으로 body에 유저의 아이디, 권한(Role)을 반환해준다.
 */
@Slf4j
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

    private final LoginFailService loginFailService;
    private final ObjectMapper objectMapper;

    public CustomAuthenticationSuccessHandler(final LoginFailService loginFailService, final ObjectMapper objectMapper) {
        this.loginFailService = loginFailService;
        this.objectMapper = objectMapper;
    }

    @Override
    public void onAuthenticationSuccess(final HttpServletRequest request, final HttpServletResponse response, final Authentication authentication) throws IOException, ServletException {
        String loginId = (String) request.getAttribute(USERNAME_VALUE);
        String clientIp = request.getRemoteUser();
        if(loginId != null){
            loginFailService.successLogin(loginId,clientIp);
        }

        SecurityContext context = saveContext(authentication);

        HttpSession session = request.getSession(true);
        session.setAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY, context);

        //CSRF 토큰을 응답 헤더에 추가
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrfToken != null) {
            response.setHeader("X-XSRF-TOKEN", csrfToken.getToken());
        }

        setCookies(request, response, session);
        setContentType(response);
        setLoginProfileToResponseBody(response, authentication);
    }

    private static SecurityContext saveContext(final Authentication authentication) {
        //인증 정보를 SecurityContext에 수동 저장
        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);
        return context;
    }

    private void setLoginProfileToResponseBody(final HttpServletResponse response, final Authentication authentication) throws IOException {
        //사용자 이름(아이디) 가져오기
        String username = authentication.getName();

        try {
            //권한 정보 추출
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
            String authority = authorities.iterator().next().getAuthority();
            //로그인 프로필 객체 생성 및 JSON 변환
            LoginProfile profile = new LoginProfile(username, authority);
            //응답에 JSON 작성
            response.getWriter().write(objectMapper.writeValueAsString(profile));
        } catch (Exception e) {
            handleException(response, e);
        }
    }

    private void handleException(final HttpServletResponse response, final Exception e) throws IOException {
        //예외 처리 및 로깅
        log.error("로그인 프로필 응답 생성 중 오류 발생", e);
        response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        ApiErrorResponse<Object> errorResponse = ApiErrorResponse.of(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "로그인 처리 중 오류가 발생했습니다. 죄송합니다.",
                null
        );
        response.getWriter().write(objectMapper.writeValueAsString(errorResponse));
    }

    private void setContentType(final HttpServletResponse response) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        response.setStatus(HttpServletResponse.SC_OK);
    }

    private void setCookies(final HttpServletRequest request, final HttpServletResponse response, final HttpSession session) {
        //세션 쿠키 내려주기
        String sessionId = session.getId();
        Cookie sessionCookie = new Cookie("JSESSIONID", sessionId);
        sessionCookie.setPath("/");
        sessionCookie.setHttpOnly(true);
        sessionCookie.setSecure(request.isSecure());
        response.addCookie(sessionCookie);
    }
}
