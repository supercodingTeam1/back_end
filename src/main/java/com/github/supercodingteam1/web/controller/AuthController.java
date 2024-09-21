package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.config.auth.dto.TokenDTO;
import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.repository.entity.user.User;
import com.github.supercodingteam1.service.AuthService;
import com.github.supercodingteam1.service.UserRoleService;
import com.github.supercodingteam1.service.UserService;
import com.github.supercodingteam1.web.dto.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
@Slf4j
public class AuthController {
    private final AuthService authService;
    private final UserService userService;

    private final UserRoleService userRoleService;

    private final JwtTokenProviderService jwtTokenProviderService;

    /**
     * 회원 가입 처리
     * @param signUpDTO 회원가입을 위한 데이터 (유효성 검사됨)
     * @return 성공 또는 실패 메시지와 상태 코드를 포함한 ResponseEntity
    {
    "user_name":"test1",
    "user_email":"test1@gmail.com",
    "user_password":"1111",
    "user_phone":"010-1111-2222",
    "user_profile":"11",
    "roles" :["BUYER", "SELLER"]
    }
     */
    @PostMapping("/signup")
    public ResponseDTO registerUser(@Valid @RequestBody SignUpDTO signUpDTO, BindingResult bindingResult) {
        log.info("회원 가입 처리 요청 수신");
        try {
            authService.signUp(signUpDTO, bindingResult);
            return ResponseDTO.builder()
                    .status(200)
                    .message("성공적으로 회원가입하였습니다.")
                    .build();
        } catch (Exception e) {
            log.error("회원가입 처리 중 오류 발생: ", e);
            ResponseDTO responseDTO = ResponseDTO.builder()
                    .status(400)
                    .message(e.getMessage())
                    .build();
            return ResponseDTO.builder()
                    .status(HttpStatus.BAD_REQUEST.value())
                    .message(e.getMessage())
                    .build();
        }
    }

    /**
     * 회원가입시 중복확인
     * @param checkDuplicateDTO
     * @return
     */
    @PostMapping("/duplicate")
    public ResponseDTO duplicate(@RequestBody CheckDuplicateDTO checkDuplicateDTO){

        log.info("회원가입시 중복확인 -duplicate :checkDuplicateDTO  :{} ", checkDuplicateDTO);

        if(StringUtils.hasText(checkDuplicateDTO.getUser_email())){
            boolean isDuplicate = userService.isDuplicateEmail(checkDuplicateDTO.getUser_email());
            if(isDuplicate){
                return  ResponseDTO.builder()
                        .status(HttpStatus.BAD_REQUEST.value())
                        .message("이미 존재하는 이메일입니다.")
                        .build();
            }
        }
        return ResponseDTO.builder()
                .status(200)
                .message("사용가능한 이메일입니다.")
                .build();

    }


    /** auth/login
     * 로그인 처리
     * @param loginDTO
     * @param bindingResult
     * @return
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginDTO loginDTO, BindingResult bindingResult){

        // 1. 유효성 체크 메서드 호출
        if (bindingResult.hasErrors()) {
            String errorMessage = Objects.requireNonNull(bindingResult.getFieldError()).getDefaultMessage();
            LoginResponseDTO<Object> loginResponseDTO =  LoginResponseDTO.builder()
                    .status(400)
                    .message(errorMessage)
                    .build();
            return ResponseEntity.badRequest().body(loginResponseDTO);
        }

        User user = userService.getByCredentials(loginDTO.getUser_name(), loginDTO.getUser_password());

        if(user!=null){
            //토큰 생성
            TokenDTO tokenDTO = jwtTokenProviderService.create(user);

            //.collect(Collectors.joining(","))  // 여러 역할을 ','로 구분하여 하나의 문자열로 합침
            //.collect(Collectors.toList())  // 리스트로 수집하여 배열 형식으로 반환
            LoginResponseDTO<?> loginResponseDTO = LoginResponseDTO.builder()
                    .status(200)
                    .message("success")
                    .data(new ResTokenDTO(
                            tokenDTO.getAccessToken(),
                            tokenDTO.getRefreshToken(),
                            user.getUser_role().stream()
                                    .map(userRole -> userRole.getRoleName().toString())  // Role 열거형을 문자열로 변환
                                    .collect(Collectors.joining(","))  // 여러 역할을 ','로 구분하여 하나의 문자열로 합침
                    ))
                    .build();

            return ResponseEntity.ok().body(loginResponseDTO);
        }else{
            LoginResponseDTO<Object> loginResponseDTO =  LoginResponseDTO.builder()
                    .status(400)
                    .message("로그인 실패")
                    .build();
            return ResponseEntity.badRequest().body(loginResponseDTO);
        }
    }

    /**   auth/logout
     * 로그 아웃 처리
     * @param loginDTO
     * @return
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody LoginDTO loginDTO){
        try{
            //DB 에서 토큰 삭제
            userService.logout(loginDTO);
            return ResponseEntity.ok().body(ResponseDTO.builder().status(200).message("Logout successful").build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseDTO.builder().status(400).message(e.getMessage()).build());
        }
    }

    // auth/withdraw
    @DeleteMapping("/withdraw")
    public ResponseEntity<?> withdraw(HttpServletRequest httpServletRequest, @RequestBody WithdrawDTO withdrawDTO){
        try{
            //TODO : httpServletRequest 헤더에서 토큰 가져와서 파싱 후 user 판단
            //DB 에서 토큰 삭제
            userService.withdraw(withdrawDTO);
            return ResponseEntity.ok().body(ResponseDTO.builder().status(200).message("success").build());
        }catch (Exception e){
            return ResponseEntity.ok().body(ResponseDTO.builder().status(400).message(e.getMessage()).build());
        }
    }

    /**
     *  // auth/refreshToken
     * @param refreshToken
     * @return
     */
    @PostMapping("/refreshToken")
    public ResponseEntity<?> reissue(@RequestHeader(value = "refreshToken") String refreshToken) {
        log.info("접근 토큰 재발행");
        try {
            UserDTO reissue = jwtTokenProviderService.reissue(refreshToken);
            LoginResponseDTO<?> loginResponseDTO = LoginResponseDTO.builder()
                    .status(200)
                    .message("success")
                    .data(new ResTokenDTO(
                            reissue.getToken().getAccessToken(),
                            reissue.getToken().getRefreshToken(),
                            String.join(",", reissue.getUser_role())
                    ))
                    .build();
            return ResponseEntity.ok().body(loginResponseDTO);

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder().status(400).message("갱신 토큰이 유효하지 않습니다.").build());
        }
    }
}
