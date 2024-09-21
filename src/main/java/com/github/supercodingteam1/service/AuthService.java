package com.github.supercodingteam1.service;


import com.github.supercodingteam1.config.security.JwtTokenProvider;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.LoginDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import com.github.supercodingteam1.web.dto.SignUpDTO;
import com.github.supercodingteam1.web.exceptions.NotAcceptException;
import com.github.supercodingteam1.web.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Service
public class AuthService {
    private static final String EMAIL_REGEX = //이메일 정규표현식 검사
            "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final PasswordEncoder passwordEncoder;

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

    public void signUp(SignUpDTO signUpDTO, BindingResult bindingResult) {
        // 유효성 검사 결과 처리
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            ResponseEntity.badRequest().body(ResponseDTO.builder().status(400).message(errorMessage).build());
        }

        else {
            // 회원가입 처리
            // 1. User 정보 유효성 체크
            String email = signUpDTO.getUser_email();
            if (email == null || email.isEmpty() || !isValidEmail(email)) {
                throw new RuntimeException("유효하지 않은 형식입니다.");
            }

            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("이메일이 이미 존재합니다.");
            }

            // 회원가입 DTO를 User 엔티티로 변환
            User user = User.builder()
                    .userName(signUpDTO.getUser_name())
                    .email(signUpDTO.getUser_email())
                    .password(passwordEncoder.encode(signUpDTO.getUser_password()))
                    .userAddress(signUpDTO.getUser_address())
                    .phoneNum(signUpDTO.getUser_phone())
                    .userGender(signUpDTO.getUser_gender())
                    .userImg(signUpDTO.getUser_profile() != null? signUpDTO.getUser_profile() : null)
                    .build();

            // 3. 유효성 통과 후 사용자 저장
            userRepository.save(user);
        }
    }

    private static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }
}