package com.github.supercodingteam1.web.dto;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class LogoutDTO {

  private Integer status;   //200
  private String message; //Logout successful


}
