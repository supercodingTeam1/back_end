package com.github.supercodingteam1.exception;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.supercodingteam1.web.dto.ResponseDTO;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * 토큰 필터 에러 처리
 */
@Component
@Log4j2
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    /**
     * 1. 에러 설정
     * @param request
     * @param response
     * @param authException
     * @throws IOException
     */
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException {

        log.info("=========  JwtAuthenticationEntryPoint getMessage  : {}", authException.getMessage());
        log.info("=========  JwtAuthenticationEntryPoint getMessage  : {}", request.getContentType());

        String errorCode = (String) request.getAttribute("errorCode");
        String message = (String) request.getAttribute("message");

        log.info("******* JwtAuthenticationEntryPoint  토큰 필터 에러 처리 : errorCode :{} , message :{}  ", errorCode, message);

        if (errorCode != null ||
                (authException.getMessage() != null && authException.getMessage().contains("authentication"))) {

            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/json");

            ResponseDTO errorRes;
            if (authException.getMessage() != null && authException.getMessage().contains("authentication")) {
                errorRes = ResponseDTO.builder().status(400).message("권한이 없습니다. 해당 리소스에 접근할 수 없습니다.").build();
            } else {
                errorRes = ResponseDTO.builder().status(403).message(message).build();
            }

            ObjectMapper objectMapper = new ObjectMapper();
            String result = objectMapper.writeValueAsString(errorRes);
            response.getWriter().print(result);
        }
    }
}
