package com.github.supercodingteam1.service;

import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.repository.entity.user.RefreshTokenRepository;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.repository.entity.user.UserRepository;
import com.github.supercodingteam1.web.dto.LoginDTO;
import com.github.supercodingteam1.web.dto.UserDTO;
import com.github.supercodingteam1.web.dto.WithdrawDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
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

  private final JwtTokenProviderService jwtTokenProviderService;



  /**
   * 회원가입 처리
   * @param user 회원가입 정보가 담긴 User 객체
   * @return 저장된 User 객체
   * @throws Exception 유효성 검사 실패 또는 중복된 사용자 존재 시 예외 발생
   */
  public User registerUser(User user) throws Exception {
    // 1. User 정보 유효성 체크
    if (user == null || user.getUserName() == null || user.getUserName().isEmpty()) {
      throw new RuntimeException("유효하지 않은 userName입니다.");
    }

    // 2. 중복된 userName 체크
    if (userRepository.existsByUserName(user.getUserName())) {
      throw new RuntimeException("userName 이 이미 존재합니다.");
    }

    if (userRepository.existsByEmail(user.getEmail())) {
      throw new RuntimeException("이메일이 이미 존재합니다.");
    }

    // 3. 유효성 통과 후 사용자 저장
    return userRepository.save(user);
  }


  /**
   * 중복된 userName 체크
   * @param userName
   * @return
   */
  public boolean isDuplicateUserName(String userName) {
    return userRepository.existsByUserName(userName);
  }


  /**
   * 중복된 이메일 체크
   * @param userEmail
   * @return
   */
  public boolean isDuplicateEmail(String userEmail) {
    return userRepository.existsByEmail(userEmail);
  }


  public User getByCredentials(String userName, String password) {
    return userRepository.findByUserNameAndPassword(userName,password);
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
  public void withdraw(WithdrawDTO withdrawDTO) {
    User user = userRepository.findByEmail(withdrawDTO.getUser_email()).orElseThrow(null);
    //1.토큰 삭제
    refreshTokenRepository.deleteByUserUserId(user.getUserId());
    
    //2.유저 삭제
    userRepository.deleteById(user.getUserId());
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
