package com.springboot.member;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class InMemoryMemberService implements MemberService {
    //데이터베이스에 연동없이 메모리에 Spring 시큐리티의 user를 등록하기 위해 필요
    private final UserDetailsManager userDetailsManager;
    //user등록시 패스워드를 암호화한 후 등록하기 위해 필요
    private final PasswordEncoder passwordEncoder;

    public InMemoryMemberService(UserDetailsManager userDetailsManager, PasswordEncoder passwordEncoder) {
        this.userDetailsManager = userDetailsManager;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Member createMember(Member member) {
        //User의 권한 목록 생성
        List<GrantedAuthority> authorities = createAuthorities(Member.MemberRole.ROLE_USER.name());

        //등록할 유저의 패스워드를 암호화
        String encryptedPassword = passwordEncoder.encode(member.getPassword());

        //Spirng Security User로 등록하기 위해서 UserDetails 생성
        //Spring Security에서는 user정보를 UserDetails로 관리한다.
        UserDetails userDetails = new User(member.getEmail(), encryptedPassword, authorities);

        //유저 등록
        userDetailsManager.createUser(userDetails);

        return member;

    }

    private List<GrantedAuthority> createAuthorities(String... roles){
        return Arrays.stream(roles)
                //해당 User의 Role을 전달
                .map(role -> new SimpleGrantedAuthority(role))
                .collect(Collectors.toList());
    }
}
