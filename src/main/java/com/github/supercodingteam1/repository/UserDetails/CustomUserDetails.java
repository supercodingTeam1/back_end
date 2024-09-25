package com.github.supercodingteam1.repository.UserDetails;

import com.github.supercodingteam1.repository.entity.user.User;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@ToString
@Slf4j
public class CustomUserDetails implements UserDetails {

    private User user;
    private Integer userId;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public Integer getUserId() {
        return userId;
    }

    public CustomUserDetails(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.password = user.getPassword();
        this.authorities = getAuthorities();
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return authorities.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        Collection<GrantedAuthority> collector=new ArrayList<>();

        log.info("********* 시큐리티 로그인 :" +user.getUser_role().toString());
        collector.add(()-> user.getUser_role().toString());
        return collector;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUserName();
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
