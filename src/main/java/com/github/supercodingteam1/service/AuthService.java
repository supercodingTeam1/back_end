package com.github.supercodingteam1.service;


import com.github.supercodingteam1.config.security.JwtTokenProvider;
import com.github.supercodingteam1.repository.entity.user.Role;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.LoginDTO;
import com.github.supercodingteam1.web.exceptions.NotAcceptException;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@RequiredArgsConstructor
@Service
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;

    public Map<String, String> login(LoginDTO loginRequestDto) {
        String email = loginRequestDto.getUser_name();
        String password = loginRequestDto.getUser_password();

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(email, password)
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);

            User user = userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));

            List<String> roles = user.getUser_role().stream().map(role->role.getRoleName().toString()).toList();

            String accessToken = jwtTokenProvider.createAccessToken(email, roles);
            String refreshToken = jwtTokenProvider.createRefreshToken();

            Map<String, String> tokens = new HashMap<>();
            tokens.put("accessToken", accessToken);
            tokens.put("refreshToken", refreshToken);

            return tokens;

        } catch (Exception e) {
            e.printStackTrace();
            throw new NotAcceptException("로그인에 실패했습니다.");
        }
    }
}