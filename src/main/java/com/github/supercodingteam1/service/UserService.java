package com.github.supercodingteam1.service;

import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.repository.entity.user.RefreshToken;
import com.github.supercodingteam1.repository.entity.user.RefreshTokenRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.LoginDTO;
import com.github.supercodingteam1.web.dto.UserDTO;
import com.github.supercodingteam1.web.dto.WithdrawDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Log4j2
@Service
@Transactional
@RequiredArgsConstructor
public class UserService {

  private final UserRepository userRepository;
  private final RefreshTokenRepository refreshTokenRepository;
  private final PasswordEncoder passwordEncoder;

  private final JwtTokenProviderService jwtTokenProviderService;

  /**
   * 중복된 이메일 체크
   * @param userEmail
   * @return
   */
  public boolean isDuplicateEmail(String userEmail) {
    return userRepository.existsByEmail(userEmail);
  }

  public User getByCredentials(String userName, String password) {
    User user = userRepository.findByEmail(userName).orElse(null);
    if(passwordEncoder.matches(password, user.getPassword())) {
      return user;
    }
    return null;
  }


  /**
   * 로그아웃처리
   * @param loginDTO
   */
  public void logout(LoginDTO loginDTO) {
    User user = userRepository.findByUserName(loginDTO.getUser_name());
    refreshTokenRepository.deleteByUserUserId(user.getUserId());
  }


  /**
   * 회원 탈퇴 처리
   * @param withdrawDTO
   */
  @Transactional
  public void withdraw(WithdrawDTO withdrawDTO, CustomUserDetails customUserDetails) {
    //1.토큰 삭제

    refreshTokenRepository.deleteByUserUserId(customUserDetails.getUserId());
    
    //2.유저 삭제
    userRepository.deleteById(customUserDetails.getUserId());
  }


  /**
   * 유저 정보 조회
   * @param token
   * @return
   */
  public UserDTO findByTokenUserInfo(String token){

    try{
      // 1. 토큰을 이용해 사용자 ID 추출
      //log.info("1. 토큰을 이용해 사용");
      String userId = jwtTokenProviderService.validateAndGetUserId(token);
      //log.info("1. 토큰을 이용해 사용자 ID 추출  :   {}" , userId);

      User user =userRepository.findByUserId(Integer.valueOf(userId));

      // 2. 유저 정보를 반환
      return UserDTO.of(user);
    }catch(Exception e){
      throw new IllegalStateException("유저 정보가 없습니다.");
    }
  }
}
