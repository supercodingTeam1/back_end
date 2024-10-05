package com.github.supercodingteam1.service;


import com.github.supercodingteam1.config.security.JwtTokenProvider;
import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.user.*;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import com.github.supercodingteam1.web.dto.SignUpDTO;
import com.github.supercodingteam1.web.exceptions.TokenExpiredException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


@RequiredArgsConstructor
@Slf4j
@Service
public class AuthService {
    private static final String EMAIL_REGEX = //이메일 정규표현식 검사
            "^[a-zA-Z0-9_+&*-]+(?:\\." +
                    "[a-zA-Z0-9_+&*-]+)*@" +
                    "(?:[a-zA-Z0-9-]+\\.)+[a-z" +
                    "A-Z]{2,7}$";

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RedisTemplate<String, String> redisTemplate;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserRoleRepository userRoleRepository;
    private final S3Uploader s3Uploader;


//    public Map<String, String> login(LoginDTO loginRequestDto) {
//        String email = loginRequestDto.getUser_email();
//        String password = loginRequestDto.getUser_password();
//
//        try {
//            Authentication authentication = authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(email, password)
//            );
//            SecurityContextHolder.getContext().setAuthentication(authentication);
//
//            User user = userRepository.findByEmail(email)
//                    .orElseThrow(() -> new NotFoundException("사용자를 찾을 수 없습니다."));
//
//            List<String> roles = user.getUser_role().stream().map(role->role.getRoleName().toString()).toList();
//
//            String accessToken = jwtTokenProvider.createAccessToken(email, roles);
//            String refreshToken = jwtTokenProvider.createRefreshToken();
//
//            Map<String, String> tokens = new HashMap<>();
//            tokens.put("accessToken", accessToken);
//            tokens.put("refreshToken", refreshToken);
//
//            return tokens;
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            throw new NotAcceptException("로그인에 실패했습니다.");
//        }
//    }
    @Transactional
    public void logout(CustomUserDetails user, String accessToken) throws TokenExpiredException {
        RefreshToken refreshToken=refreshTokenRepository.findTopByUserUserIdOrderByRefreshTokenIdDesc(user.getUserId());

        log.info("Access Token: {}", accessToken);

//        // 사용자 이름을 이용해 비관적 락을 걸고 사용자 정보 조회
//        if(userRepository.findByUserId(user.getUserId())!=null){
//            throw new UsernameNotFoundException("해당하는 UserId를 찾을 수 없습니다.");
//        }

        // 엑세스 토큰을 블랙리스트에 추가
        redisTemplate.opsForValue().set(accessToken, "blacklisted", 3600, TimeUnit.SECONDS);

//        //refreshToken이 이미 삭제되었는데 다시 로그아웃 요청을 보냈을 때
//        if (!refreshTokenRepository.existsByUserUserId(user.getUserId())) {
//            throw new IllegalStateException("이미 로그아웃된 사용자입니다.");
//        }
//        //refreshToken이 이미 만료된 경우
//        if (jwtTokenProvider.isRefreshTokenExpired(refreshToken)) {
//            throw new TokenExpiredException("이미 만료된 토큰입니다.");
//        }

        refreshTokenRepository.deleteAllByUserUserId(user.getUserId());

        //로그아웃 기록 조회
        log.info("User {} logged out successfully", user.getUserId());

    }

    @Transactional
    public void signUp(MultipartFile profileImage, SignUpDTO signUpDTO, BindingResult bindingResult) throws IOException {
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
                throw new RuntimeException("유효하지 않은 이메일 형식입니다.");
            }

            if (userRepository.existsByEmail(email)) {
                throw new RuntimeException("이메일이 이미 존재합니다.");
            }

            // 회원가입 DTO를 User 엔티티로 변환
            System.out.println(signUpDTO.getRoles().stream().map(Role::toString).toList());

            List<UserRole> userRoleList = signUpDTO.getRoles().stream()
                    .map(userRoleRepository::findByRoleName).toList();

            userRoleRepository.saveAll(userRoleList);

            User user = User.builder()
                    .userName(signUpDTO.getUser_name())
                    .email(signUpDTO.getUser_email())
                    .password(passwordEncoder.encode(signUpDTO.getUser_password()))
                    .userAddress(signUpDTO.getUser_address())
                    .phoneNum(signUpDTO.getUser_phone())
                    .userImg(profileImage != null? setProfileImage(profileImage) : null)
                    .user_role(userRoleList)
                    .build();

            // 3. 유효성 통과 후 사용자 저장
            userRepository.save(user);
        }
    }

    private String setProfileImage(MultipartFile profileImage) throws IOException {
        //프로필 사진 S3 업로드
        return s3Uploader.upload(profileImage, "userProfileImage");
    }

    private static boolean isValidEmail(String email) {
        Pattern pattern = Pattern.compile(EMAIL_REGEX);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }


}