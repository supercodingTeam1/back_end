package com.github.supercodingteam1.web.dto;


import lombok.*;

@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WithdrawDTO {

  private String user_email;
  private String token;


}
