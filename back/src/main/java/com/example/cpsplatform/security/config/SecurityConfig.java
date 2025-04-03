package com.example.cpsplatform.security.config;

import com.example.cpsplatform.member.repository.MemberRepository;
import com.example.cpsplatform.security.filter.JsonUsernamePasswordAuthenticationFilter;
import com.example.cpsplatform.security.handler.CustomAuthenticationFailHandler;
import com.example.cpsplatform.security.handler.CustomAuthenticationSuccessHandler;
import com.example.cpsplatform.security.provider.UsernamePasswordAuthenticationTokenProvider;
import com.example.cpsplatform.security.service.CustomUserDetailService;
import com.example.cpsplatform.security.service.LoginFailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


import java.util.List;


@Configuration
public class SecurityConfig {

    public static final String COOKIES_JSESSIONID = "JSESSIONID";
    public static final String USERNAME_VALUE = "username";
    public static final String PASSWORD_VALUE = "password";


    @Autowired
    MemberRepository memberRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    ObjectMapper objectMapper;
    @Autowired
    LoginFailService loginFailService;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        return http.csrf(csrf ->
                        csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()) // session 기반이라 csrf 체크를 해야함
                                .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler())
                )
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers("/api/admin/**").hasRole("ADMIN"); // 어드민만 접근가능
                })
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .httpBasic(AbstractHttpConfigurer::disable) //
                .formLogin(AbstractHttpConfigurer::disable) //JSON 기반 필터로 로그인해서 삭제
                .authorizeHttpRequests(authorize -> {
                    authorize.requestMatchers(HttpMethod.GET, //인증 없이 접근 가능한 Get 메소드 url
                                    "/video/**","/images/**","/api/auth/**", "/api/test/**","/certificate"
                            ).permitAll()
                            .requestMatchers(HttpMethod.POST,//인증 없이 접근 가능한 Post 메소드 url
                                    "/api/auth/**", "/api/test/**", "/api/v1/members","/api/v1/send-auth-code",
                                    "/api/v1/find-id"
                            ).permitAll()
                            .anyRequest().authenticated(); // 나머지 url은 인증 필요
                })

                .logout(logout ->
                        logout.logoutUrl("/api/auth/logout") // 로그아웃 api url
                                .invalidateHttpSession(true)  // 로그아웃 시 저장된 세션을
                                .deleteCookies(COOKIES_JSESSIONID) // 브라우저 세션 제거
                )
                //폼 로그인 필터 앞에 필터를 추가
                .addFilterBefore(jsonUsernamePasswordAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class)
                .build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(List.of("http://localhost:3000"));
        config.setAllowedMethods(List.of("HEAD", "POST", "GET", "DELETE", "PUT"));
        config.setAllowedHeaders(List.of("*"));
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", config);
        return source;
    }

    public AuthenticationManager authenticationManager() {
        return new ProviderManager(usernamePasswordAuthenticationTokenProvider());
    }

    public AuthenticationProvider usernamePasswordAuthenticationTokenProvider(){
        return new UsernamePasswordAuthenticationTokenProvider(userDetailsService(),passwordEncoder,loginFailService);
    }

    public UserDetailsService userDetailsService(){
        return new CustomUserDetailService(memberRepository);
    }

    public AbstractAuthenticationProcessingFilter jsonUsernamePasswordAuthenticationFilter(){
        JsonUsernamePasswordAuthenticationFilter jsonLoginFilter = new JsonUsernamePasswordAuthenticationFilter(
                "/api/auth/login" //api 로그인 주소 설정
        );

        jsonLoginFilter.setAuthenticationManager(authenticationManager());

        //로그인 성공시 핸들링할 핸들러 지정
        jsonLoginFilter.setAuthenticationSuccessHandler(customAuthenticationSuccessHandler());
        //로그인 실패시 핸들링할 핸들러 지정
        jsonLoginFilter.setAuthenticationFailureHandler(customAuthenticationFailureHandler());

        return jsonLoginFilter;
    }

    public AuthenticationSuccessHandler customAuthenticationSuccessHandler(){
        return new CustomAuthenticationSuccessHandler(loginFailService);
    }

    public AuthenticationFailureHandler customAuthenticationFailureHandler(){
        return new CustomAuthenticationFailHandler(objectMapper,loginFailService);
    }

}
