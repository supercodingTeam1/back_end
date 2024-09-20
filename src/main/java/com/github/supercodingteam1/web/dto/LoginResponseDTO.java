package com.github.supercodingteam1.web.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LoginResponseDTO<T> {

     private Integer status;   //200
     private String message;   //sucess
     private T data;
}
