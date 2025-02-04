package com.springboot.audit.utils;

import com.springboot.audit.HelloUserDetailsServiceV2;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Collection;
import java.util.Optional;

//스프링 시큐리티가 아닌, 개발자가 직접 로그인인증을 하기위한 구현 클래스
//AuthenticationProvider를 구현한 구현 클래스가 Bean으로 등록되어 있다면
//Spring Security가 이를 이용하여 인증을 진행한다.
public class HelloAuthenticationProviderV1 implements AuthenticationProvider {
    private final HelloUserDetailsServiceV2 userDetailsService;
    private final PasswordEncoder passwordEncoder;

    public HelloAuthenticationProviderV1(HelloUserDetailsServiceV2 userDetailsService, PasswordEncoder passwordEncoder) {
        this.userDetailsService = userDetailsService;
        this.passwordEncoder = passwordEncoder;
    }

    //사용자의 인증 여부를 결정하는 메서드
    //인증 처리 로직
    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        //authentication을 캐스팅하여 토큰을 얻음
        UsernamePasswordAuthenticationToken authToken = (UsernamePasswordAuthenticationToken) authentication;

        //Username이 존재하는지 체크한다. 비어있으면 예외를 던짐
        String username = authToken.getName();
        Optional.ofNullable(username).orElseThrow(()->
                //보안상 무엇이 틀렸는지 특정하지 말아야한다.
                new UsernameNotFoundException("Invalid User name or User Password"));
        try{
            //존재하면, userDetailsService를 이용해 DB에서 해당 사용자를 조회한다.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            //로그인 정보에 포함된 패스워드와 DB에 저장된 사용자의 패스워드가 일치하는지 검증
            String password = userDetails.getPassword();
            verifyCredentials(authToken.getCredentials(), password);

            //인증에 성공하였으므로 해당 사용자의 권한을 생성
            Collection<? extends GrantedAuthority> authorities = userDetails.getAuthorities();

            //인증된 사용자의 인증 정보를 리턴값으로 전달
            return UsernamePasswordAuthenticationToken.authenticated(username, password, authorities);
        }catch (Exception e){
            //Spring Security가 관리하는 예외를 던져준다.
            throw new UsernameNotFoundException(e.getMessage());
        }
    }


    //Spring Security 어떤 인증방식을 사용하고 있는지 전달한다.
    @Override
    public boolean supports(Class<?> authentication) {
        return UsernamePasswordAuthenticationToken.class.equals(authentication);
    }

    //패스워드 검증, 해당 검증을 통과하면 로그인 인증에 성공
    private void verifyCredentials(Object credentials, String password){
        if(!passwordEncoder.matches((String) credentials,password)){
            //왜 패스워드 검증을 하는건데 User name이 틀렸다고 메세지를 보낼까?
            //보안상 무엇이 틀렸는지 특정하지 말아야한다.
            throw new BadCredentialsException("Invalid User name or User Password");
        }
    }
}
