package com.springboot.audit;

import com.springboot.audit.utils.HelloAuthorityUtils;
import com.springboot.exception.BusinessLogicException;
import com.springboot.exception.ExceptionCode;
import com.springboot.member.Member;
import com.springboot.member.MemberRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Component
public class HelloUserDetailsServiceV2 implements UserDetailsService {
    private final MemberRepository memberRepository;
    private final HelloAuthorityUtils authorityUtils;

    public HelloUserDetailsServiceV2(MemberRepository memberRepository, HelloAuthorityUtils authorityUtils) {
        this.memberRepository = memberRepository;
        this.authorityUtils = authorityUtils;
    }


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<Member> optionalMember = memberRepository.findByEmail(username);
        Member findMember = optionalMember.orElseThrow(() ->
                new BusinessLogicException(ExceptionCode.MEMBER_NOT_FOUND));

        //이전에는 리턴값으로 User 객체를 만들어 리턴했지만
        //개선한 버전에서는 Custom UserDetails 클래스의 생성자로 findMember를 전달한다. (가독성 향상)
        return new HelloUserDetails(findMember);
    }

    //UserDetails 인터페이스를 (구현체)구현하며, Member엔티티 클래스를 상속받음
    //DB에서 조회한 회원 정보를 시큐리티의 User 정보로 변환하는 과정과 User 권한 정보를 생성하는 과정 캡슐화
    private final class HelloUserDetails extends Member implements UserDetails{
        public HelloUserDetails(Member member) {
            setEmail(member.getEmail());
            setFullName(member.getFullName());
            //이미 데이터베이스에 저장할 때 암호화된 상태
            setPassword(member.getPassword());
            setMemberId(member.getMemberId());
            //DB에 저장되어있는 Role 목록을 가져와 저장한다.
            setRoles(member.getRoles());
        }

        //User의 권한 정보를 생성한다.
        @Override
        public Collection<? extends GrantedAuthority> getAuthorities() {
            //저장한 DB의 Role 정보를 기반으로 권한 정보를 생성한다.
            return authorityUtils.createAuthorities(this.getRoles());

            //유저의 이메일을 통해 해당 이메일에 따라 권한 정보를 생성
            //return authorityUtils.createAuthorities(this.getEmail());
        }

        @Override
        public String getUsername() {
            return this.getEmail();
        }

        @Override
        public boolean isAccountNonExpired() {
            return true;
        }

        @Override
        public boolean isAccountNonLocked() {
            return true;
        }

        @Override
        public boolean isCredentialsNonExpired() {
            return true;
        }

        @Override
        public boolean isEnabled() {
            return true;
        }
    }
}
