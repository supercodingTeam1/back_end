package com.github.supercodingteam1.service;


import com.github.supercodingteam1.config.security.JwtTokenProvider;
import com.github.supercodingteam1.repository.user.User;
import com.github.supercodingteam1.repository.user.UserRepository;
import com.github.supercodingteam1.web.dto.LoginRequestDto;
import com.github.supercodingteam1.web.exceptions.NotAcceptException;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;


@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public String login(LoginRequestDto loginRequestDto) {
        String email = loginRequestDto.getEmail();
        String password = loginRequestDto.getPassword();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext()
                    .setAuthentication(authentication);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("UserPrincipal 을 찾을 수 없습니다."));

            List<String> roles = user.getRoles();

            return jwtTokenProvider.createToken(email, roles);
        } catch (Exception e){
            e.printStackTrace();
            throw new NotAcceptException("로그인 할 수 없습니다.");
        }

    }
}
