package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.AuthService;
import com.github.supercodingteam1.web.dto.LoginDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;
import java.util.Map;
@RequiredArgsConstructor
@RestController
@RequestMapping(value = "/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public ResponseEntity<Map<String, String>> login(@RequestBody LoginDTO loginRequestDto) {
        try {
            Map<String, String> tokens = authService.login(loginRequestDto);

            return ResponseEntity
                    .status(HttpStatus.OK)
                    .header("Authorization", "Bearer " + tokens.get("accessToken"))
                    .header("Refresh-Token", tokens.get("refreshToken"))
                    .body(Collections.singletonMap("message", "로그인에 성공했습니다."));

        } catch (Exception e) {
            return ResponseEntity
                    .status(HttpStatus.UNAUTHORIZED)
                    .body(Collections.singletonMap("message", "로그인에 실패했습니다."));
        }
    }
}
