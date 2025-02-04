package com.springboot.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;


@Configuration
public class SecurityConfguration {
    //이곳에 Spring Security 설정을 한다.

    //HTTP 보안 구성 기본
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception{
        //HttpSecurity를 통해 HTTP 요청에 대한 보안 설정을 구성
        httpSecurity
                //동일 출처로부터 들어오는 requset만 페이지 렌더링을 허용한다.
                .headers().frameOptions().sameOrigin() // h2웹 콘솔 설정 (DB에 접속할 수 있도록 열어줌)
                .and()
                //만약 아래 코드를 설정하지 않으면 403에러가 발생한다.
                .csrf().disable() // CSRF 공격에 대한 Spring Security에 대한 설정을 비활성화

                .formLogin()                    // 기본적인 인증 방법을 폼 로그인 방식으로 지정
                .loginPage("/auths/login-form") // 커스텀 로그인 페이지를 사용하도록 설정

                //Spring Security 로그인 인증처리를 하기위한 요청 URI
                .loginProcessingUrl("/process_login")                     // 로그인 인증 요청을 수행할 URL 지정
                .failureUrl("/auths/login-form?error") // 로그인 인증 실패할 경우 리다이렉트할 주소 지정
                .and()

                .logout()               // 로그아웃 설정을 위한 LogoutConfigurer를 리턴한다.
                .logoutUrl("/logout")   // 사용자가 로그아웃을 수행하기 위한 requset URL를 지정
                .logoutSuccessUrl("/")  // 로그아웃을 성공적으로 수행한 이후 리다이렉트할 URL 지정
                .and()

                //권한이 없는 사용자가 특정 requset URI에 접근할 경우 403에러를 처리하기 위한 페이지 설정
                // 즉, 권한이 없는 사용자가 requset URI에 요청할 경우 이 페이지의 화면이 표시된다.
                // exceptionHandling()메서드는 예외를 처리하는 기능을 하며 객체를 리턴하며 구체적인 예외처리를 한다.
                .exceptionHandling().accessDeniedPage("/auths/access-denied")
                .and() //Spring Security 보안 설정을 메서드 체인 형태로 구성하도록 해주는 메서드

                // 클라이언트의 요청이 들어오면 접근권한을 확인하는 메서드
                .authorizeHttpRequests(authorize -> authorize //람다 표현식을 통해 request URI에 대한 접근권한을 부여
                        //antMatchers() 메서드는 ant빌드 툴에서 사용되는 Path Pattern을 이용해 매치되는 URL를 표현한다.
                        //무조건 더 구체적인 URL 경로부터 접근권한을 부여한 다음 덜 구체적인 URL 경로에 접근권한을 부여해야 한다.
                        .antMatchers("/orders/**").hasRole("ADMIN")      //ADMIN Role을 부여받은 사용자만 /orders로 시작하는 모든 하위 URL 접근가능
                        .antMatchers("/members/my-page").hasRole("USER") //USER 권한을 받으면 /member/my-page만 접근 가능
                        .antMatchers("/**").permitAll()                  //앞에서 지정한 URL 이외 나머지 모든 URL은 Role(접근권한)에 상관없이 접근 가능
                );

//                .anyRequest()  // 클라이언트의 모든 요청을 허용한다.
//                .permitAll();  // 모든 요청을 허용해주는것은 옳은 방법이 아니다.

        return httpSecurity.build();
    }
//    //InMemory User로 인증
//    @Bean
//    public UserDetailsManager userDetailsService(){
//        //UserDetails 인터페이스는 인증된 사용자의 핵심 정보를 포함한다.
//        UserDetails user =
//                //UserDetails 구현체인 User 클래스를 이용해서 사용자의 인증 정보를 생성한다.
//                //디폴트 패스워드 인코더를 이용해 사용자 패스워드를 암호화
//                User.withDefaultPasswordEncoder()
//                        .username("email@email")
//                        .password("1111")
//                        .roles("USER")
//                        .build();
//       //관리자 계정
//        UserDetails admin =
//                User.withDefaultPasswordEncoder()
//                        .username("admin@gmail.com")
//                        .password("2222")
//                        .roles("ADMIN")
//                        .build();
//
//
//        // UserDetailsManager 객체를 Bean으로 등록하면 Spring에서는 해당
//        // Bean이 가지고 있는 사용자의 인증 정보가 클라이언트의 요청으로 넘어올 경우
//        // 정상적인 인증 프로세스를 수행
//        return new InMemoryUserDetailsManager(user, admin);
//    }

    //PasswordEncoder는 Spring Security에서 제공하는 패스워드 암호화 기능을 제공하는 컴포넌트
    //회원 가입 폼에서 전달받은 패스워드는 InMemory User로 등록하기 전에 암호화
    @Bean
    public PasswordEncoder passwordEncoder(){
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
