package com.github.supercodingteam1.config.auth;

import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
@Slf4j
@Transactional
public class PrincipalDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    //1.패스워드는 알아서 체킹하니깐 신경쓸 필요 없다
    //2.리턴이 잘되면 자동으로 User세션을 만든다.

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        User user = userRepository.findByUserId(Integer.valueOf(userId));
        if (user == null) {
            throw new UsernameNotFoundException(userId);
        }
        log.info("**** 로그인 성공  ");
        return new PrincipalDetails(user);
    }



}
