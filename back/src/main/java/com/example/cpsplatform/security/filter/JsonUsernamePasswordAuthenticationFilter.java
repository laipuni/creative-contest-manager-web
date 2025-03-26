package com.example.cpsplatform.security.filter;

import com.example.cpsplatform.exception.security.InvalidRequestException;
import com.example.cpsplatform.security.domain.LoginRequest;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import java.io.IOException;

@Slf4j
public class JsonUsernamePasswordAuthenticationFilter extends AbstractAuthenticationProcessingFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    public JsonUsernamePasswordAuthenticationFilter(final String loginUrl) {
        super(new AntPathRequestMatcher(loginUrl));
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response){
        if (!request.getMethod().equals("POST")) {
            throw new AuthenticationServiceException("Authentication method not supported: " + request.getMethod());
        }

        String contentType = request.getContentType();
        if(contentType == null || !contentType.startsWith(MediaType.APPLICATION_JSON_VALUE)){
            throw new AuthenticationServiceException("Authentication Content type supported application/json");
        }

        LoginRequest loginRequest = getLoginRequest(request);
        UsernamePasswordAuthenticationToken authRequest =
                new UsernamePasswordAuthenticationToken(
                        loginRequest.getUsername(),
                        loginRequest.getPassword()
                );
        return getAuthenticationManager().authenticate(authRequest);
    }

    public LoginRequest getLoginRequest(HttpServletRequest request){
        try {
            return objectMapper.readValue(request.getInputStream(), LoginRequest.class);
        } catch (IOException e) {
            log.warn("로그인 요청 파라미터 파싱 실패", e);
            throw new InvalidRequestException("로그인 요청 파싱 중 오류 발생", e);
        }
    }

}
