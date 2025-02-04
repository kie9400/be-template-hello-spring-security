package com.springboot.audit;

import com.springboot.audit.utils.HelloAuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.Member;
import com.springboot.member.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

//import java.util.Collection;
//import java.util.Optional;
//
////데이터베이스에서 조회한 User의 인증 정보를 기반으로 인증을 처리하는 클래스
////Custom UserDetailsService를 구현하기 위해서는 인터에피스를 구현해야한다.
//@Component
//public class HelloUserDetailsServiceV1 implements UserDetailsService {
//    private final MemberRepository memberRepository;
//    private final HelloAuthorityUtils authorityUtils;
//
//    public HelloUserDetailsServiceV1(MemberRepository memberRepository, HelloAuthorityUtils authorityUtils) {
//        this.memberRepository = memberRepository;
//        this.authorityUtils = authorityUtils;
//    }
//
//    @Override
//    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<Member> optionalMember = memberRepository.findByEmail(username);
//        Member findMember = optionalMember.orElseThrow(
//                ()-> new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));
//
//        //조회한 회원의 이메일을 이용해 Role 기반의 권한 정보(GrantedAuthority) 컬렉션을 생성한다.
//        Collection<? extends GrantedAuthority> authorities = authorityUtils.createAuthorities(findMember.getEmail());
//
//        //DB에서 조회한 정보와 권한 정보를 Spring 시큐리티에 정보를 제공해주어야 한다.
//        //UserDetails인터페이스의 구현체인 User 클래스의 객체를 통해 정보를 제공한다.
//        //즉, DB에서 유저의 인증 정보만 Spring Security에게 넘겨주고, 인증 처리는 시큐리티가 대신 해준다.
//        return new User(findMember.getEmail(), findMember.getPassword(), authorities);
//    }
//}
