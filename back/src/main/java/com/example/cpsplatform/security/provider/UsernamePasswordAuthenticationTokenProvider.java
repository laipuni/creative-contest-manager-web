package com.example.cpsplatform.security.provider;

import com.example.cpsplatform.exception.LoginFailedException;
import com.example.cpsplatform.exception.SignupNotCompletedException;
import com.example.cpsplatform.security.domain.SecurityMember;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

@Slf4j
public class UsernamePasswordAuthenticationTokenProvider implements AuthenticationProvider {

    private final UserDetailsService userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public UsernamePasswordAuthenticationTokenProvider(final UserDetailsService userDetailsService, final PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {
        String loginId = (String) authentication.getPrincipal();
        String password = (String) authentication.getCredentials();
        SecurityMember securityMember = (SecurityMember) userDetailsService.loadUserByUsername(loginId);

        if(!securityMember.isAccountNonLocked()){
            //계정이 잠겼을 경우
            //todo 계정이 잠겼다는 예외 발생
        }

        if(!passwordEncoder.matches(password,securityMember.getPassword())){
            //비밀번호가 일치하지 않는다면 예외 발생
            throw new LoginFailedException();
        }

        if(!securityMember.isSignupComplete()){
            //아직 회원가입 인증이 하지 않은 경우
            throw new SignupNotCompletedException();
        }


        return new UsernamePasswordAuthenticationToken(securityMember,null,securityMember.getAuthorities());
    }

    @Override
    public boolean supports(final Class<?> authentication) {
        //해당 클래스의 토큰을 지원하는지 확인
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication);
    }
}
