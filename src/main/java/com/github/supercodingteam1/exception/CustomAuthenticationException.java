package com.github.supercodingteam1.exception;


import lombok.*;
import org.springframework.security.core.AuthenticationException;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CustomAuthenticationException extends RuntimeException  {

    private String message;
    private String errorCode;

}