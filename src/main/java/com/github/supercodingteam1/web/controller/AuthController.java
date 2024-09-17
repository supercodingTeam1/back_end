package com.github.supercodingteam1.web.controller;

import com.github.supercodingteam1.service.AuthService;
import com.github.supercodingteam1.web.dto.LoginRequestDto;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping(value="/auth")
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "/login")
    public String login(@RequestBody LoginRequestDto loginRequestDto, HttpServletResponse httpServletResponse){
        String token=authService.login(loginRequestDto);
        httpServletResponse.setHeader("X-AUTH-TOKEN",token);
        return "로그인에 성공했습니다.";
    }
}
