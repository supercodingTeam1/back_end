package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.config.auth.jwt.JwtTokenProviderService;
import com.github.supercodingteam1.repository.UserDetails.CustomUserDetails;
import com.github.supercodingteam1.service.UserService;
import com.github.supercodingteam1.web.dto.ItemDetailDTO;
import com.github.supercodingteam1.web.dto.MyPageDTO;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import com.github.supercodingteam1.web.dto.UserDTO;

import com.github.supercodingteam1.web.exceptions.NotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/mypage")
@RequiredArgsConstructor
@Log4j2
public class MyPageController {

    private final UserService userService;
    private final JwtTokenProviderService jwtTokenProviderService;


    /**
     *  http://localhost:8080/mypage
     * @param request
     * @return
     */
    @GetMapping
    public ResponseEntity<?> myInfo(HttpServletRequest request) {
        log.info("myInfo 요청");

        try {
            // 1. Authorization 헤더에서 JWT 토큰 추출
            String token = request.getHeader("X-AUTH-TOKEN");
            if (token == null) {
                return ResponseEntity.status(401).body("토큰이 존재하지 않습니다.");
            }

//            String token = authorizationHeader.substring(7); // "Bearer " 이후의 토큰 부분만 추출

            // 2. 토큰을 이용해 사용자 ID 추출 후 사용자 ID로 사용자 정보 조회
            UserDTO userDTO = userService.findByTokenUserInfo(token);

            // 3. 사용자 정보를 "user_info"로 감싸서 반환
            Map<String, Object> userInfoMap = new HashMap<>();
            userInfoMap.put("user_info", userDTO);

            MyPageDTO<Map<String, Object>> myPageDTO = MyPageDTO.<Map<String, Object>>builder()
                    .status(200)
                    .message("success")
                    .data(userInfoMap)
                    .build();

            return ResponseEntity.ok().body(myPageDTO);

        } catch (Exception e) {
            log.error("사용자 정보를 가져오는 중 오류 발생: ", e);
            return ResponseEntity.status(500).body("사용자 정보를 가져오는 중 오류가 발생했습니다.");
        }
    }

    @Operation(summary = "마이페이지 주문내역 조회")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "성공적으로 조회했습니다."),
            @ApiResponse(responseCode = "404", description = "주문 내역을 찾을 수 없습니다."),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping("/order")
    public ResponseEntity<?> MyBuyInfo(@AuthenticationPrincipal CustomUserDetails userDetails) {
        log.info("my buy info 요청");

        if (userDetails == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("인증된 사용자가 아닙니다.");
        }
        MyPageDTO<?> myInfo=userService.getMyBuyInfo(userDetails);
        return ResponseEntity.ok().body(myInfo);
}
