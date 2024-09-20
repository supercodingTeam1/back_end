package com.github.supercodingteam1.config.auth;

import com.github.supercodingteam1.repository.entity.user.User;
import lombok.Getter;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;


import java.io.Serial;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

//Security Session => Authentication   => UserDetails
@Getter
@Log4j2
public class PrincipalDetails implements UserDetails{

    @Serial
    private static final long serialVersionUID = 1L;
    private final User user; // 콤포지션
    private final Integer userId;
    private final String idStr; //아이디를 문자열로 반환
    private final String email;


    /** 다음은  OAuth2User 를 위한 필드 */
    private String authProviderId;
    private Collection<? extends GrantedAuthority> authorities;
    private Map<String, Object> attributes;


    public PrincipalDetails(User user, Map<String, Object> attributes){
        this.attributes=attributes;
        this.authorities= Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER"));

        this.userId=user.getUserId();
        this.idStr=user.getUserId().toString();
        this.email = user.getEmail();
        this.user = user;
    }


    // 일반로그인
    public PrincipalDetails(User user) {
        this.userId=user.getUserId();
        this.idStr=user.getUserId().toString();
        this.email = user.getEmail();
        this.user = user;
        this.authorities=getAuthorities();
    }



    /**
     * 사용자에게 부여된 권한을 반환합니다. null을 반환할 수 없습니다.
     */
    // 해당 User 의 권한을 리턴하는 곳!!
    //권한:한개가 아닐 수 있음.(3개 이상의 권한)
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collector=new ArrayList<>();

        log.info("********* 시큐리티 로그인 :" +user.getUser_role().toString());
        collector.add(()-> user.getUser_role().toString());
        return collector;
    }




    /**
     * 사용자를 인증하는 데 사용된 암호를 반환합니다.
     */
    @Override
    public String getPassword() {
        return user.getPassword();
    }

    /**
     * 사용자를 인증하는 데 사용된 사용자 이름을 반환합니다. null을 반환할 수 없습니다.
     */
    @Override
    public String getUsername() {
        return user.getUserName();
    }

    /**
     * 사용자의 계정이 만료되었는지 여부를 나타냅니다. 만료된 계정은 인증할 수 없습니다.
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 사용자가 잠겨 있는지 또는 잠금 해제되어 있는지 나타냅니다. 잠긴 사용자는 인증할 수 없습니다.
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * 사용자의 자격 증명(암호)이 만료되었는지 여부를 나타냅니다. 만료된 자격 증명은 인증을 방지합니다.
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * 사용자가 활성화되었는지 비활성화되었는지 여부를 나타냅니다. 비활성화된 사용자는 인증할 수 없습니다.
     */
    @Override
    public boolean isEnabled() {
        // 우리 사이트 1년동안 회원이 로그인을 안하면!! 휴먼 계정으로 하기로 함.
        // 현재시간-로긴시간=>1년을 초과하면 return false;
        return true;
    }








}