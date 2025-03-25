package com.example.cpsplatform.security.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
public class SecurityConfig {

    public static final String COOKIES_JSESSIONID = "JSESSIONID";

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // session 기반이라 csrf 체크를 해야함
                )
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/api/admin/**").hasRole("ADMIN"); // 어드민만 접근가능
                })
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(
                                    "/video/**","/images/**","/api/auth/**", "/api/test/**","/certificate"
                            ).permitAll() // 해당 url들은 접근가능
                            .anyRequest()
                            .authenticated(); // 나머지 url은 인증 필요
                })
                .logout(logout ->
                        logout.logoutUrl("/api/auth/logout") // 로그아웃 api url
                                .invalidateHttpSession(true)  // 로그아웃 시 저장된 세션을
                                .deleteCookies(COOKIES_JSESSIONID) // 브라우저 세션 제거
                )
                .build();
    }

}
