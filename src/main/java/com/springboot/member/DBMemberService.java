package com.springboot.member;

import com.springboot.audit.utils.HelloAuthorityUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DBMemberService implements MemberService {
    private final MemberRepository memberRepository;
    private final PasswordEncoder passwordEncoder;
    //회원 가입시 User의 권한정보를 db에 저장하기 위해 해당 클래스를 주입받는다
    private final HelloAuthorityUtils authorityUtils;

    public DBMemberService(MemberRepository memberRepository, PasswordEncoder passwordEncoder, HelloAuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.passwordEncoder = passwordEncoder;
        this.authorityUtils = authorityUtils;
    }

    @Override
    public Member createMember(Member member) {
        //PasswordEncoder를 이용해 패스워드를 암호화
        String encryptedPassword = passwordEncoder.encode(member.getPassword());
        //암호화된 패스워드를 passworde 필드에 다시 할당
        member.setPassword(encryptedPassword);

        List<String> roles = authorityUtils.createAuthorities(member.getEmail());
        member.setRoles(roles);

        Member savedMember = memberRepository.save(member);
        return savedMember;
    }
}
